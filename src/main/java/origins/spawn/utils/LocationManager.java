package origins.spawn.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import origins.spawn.main.OriginsSpawn;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationManager {

  public final OriginsSpawn plugin;
  public final CooldownManager cooldownManager;
  public final ConsoleCommandSender console;

  public LocationManager(OriginsSpawn plugin) {
    this.plugin = plugin;
    this.cooldownManager = plugin.getCooldownManager();
    this.console = Bukkit.getConsoleSender();
  }

  public void teleportSpawn(Player player) {
    UUID uuid = player.getUniqueId();
    Location spawn = getSpawn();

    if (cooldownManager.hasCooldown(uuid)) {
      player.sendMessage("§cVocê já está em um teletransporte.");
      return;
    }
    if (spawn == null) {
      player.sendMessage("§cLocal de origem não encontrado.");
      return;
    }
    if (player.hasPermission("origins.spawn.bypass")) {
      player.teleport(getSpawn());
      player.sendMessage("§aTeletransportado para o inicio.");
    } else {
      startTeleportCountdown(player, uuid, spawn);
    }
  }


  private void startTeleportCountdown(Player player, UUID uuid, Location spawn) {
    cooldownManager.setCooldown(uuid, player.getLocation());

    new BukkitRunnable() {
      int countdown = plugin.getConfig().getInt("teleportCountdown", 3);

      @Override
      public void run() {
        if (cooldownManager.hasCooldown(uuid)) {
          if (countdown > 0) {
            player.sendMessage("§aTeletransportando em " + countdown + " segundo(s). Por favor, não se mova.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
            countdown--;
          } else {
            player.teleport(spawn);
            player.sendMessage("§aTeletransportado para o inicio.");
            cooldownManager.removeCooldown(uuid);
            cancel();
          }
        } else {
          cooldownManager.removeCooldown(uuid);
          cancel();
        }
      }
    }.runTaskTimer(OriginsSpawn.getPlugin(), 0L, 20);
  }

  public Location getSpawn() {
    final String CONFIG_PREFIX = "spawn.coordinates.";
    double x = plugin.getConfig().getDouble(CONFIG_PREFIX + "x");
    double y = plugin.getConfig().getDouble(CONFIG_PREFIX + "y");
    double z = plugin.getConfig().getDouble(CONFIG_PREFIX + "z");
    float yaw = (float) plugin.getConfig().getDouble(CONFIG_PREFIX + "yaw");
    float pitch = (float) plugin.getConfig().getDouble(CONFIG_PREFIX + "pitch");
    String worldName = plugin.getConfig().getString(CONFIG_PREFIX + "world");

    if (worldName == null) {
      console.sendMessage("§c" + plugin.getName() + ": Nome do mundo indefinido.");
      return null;
    }
    World world = Bukkit.getWorld(worldName);

    if (world == null) {
      console.sendMessage("§c" + plugin.getName() + ": Mundo inválido.");
      return null;
    }

    return new Location(world, x, y, z, yaw, pitch);
  }

  public void setSpawn(Player player) {
    String CONFIG_PREFIX = "spawn.coordinates.";
    Location loc = player.getLocation();
    String world = Objects.requireNonNull(loc.getWorld()).getName();
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();
    float yaw = getCardinalYaw(loc.getYaw());

    plugin.getConfig().set(CONFIG_PREFIX + "x", x);
    plugin.getConfig().set(CONFIG_PREFIX + "y", y);
    plugin.getConfig().set(CONFIG_PREFIX + "z", z);
    plugin.getConfig().set(CONFIG_PREFIX + "yaw", yaw);
    plugin.getConfig().set(CONFIG_PREFIX + "world", world);
    plugin.getConfig().set(CONFIG_PREFIX + "pitch", 0);

    try {
      OriginsSpawn.getPlugin().saveConfig();
      player.sendMessage("§aLocal de origem definido.");
    } catch (Exception exception) {
      player.sendMessage("§cOcorreu um erro ao definir o spawn.");
    }
  }

  public void deleteSpawn(CommandSender sender) {
    if (getSpawn() == null) {
      sender.sendMessage("§cLocal de origem não encontrado.");
      return;
    }
    try {
      plugin.getConfig().set("spawn", null);
      plugin.saveConfig();
      sender.sendMessage("§aLocal de origem removido");
    } catch (Exception exception) {
      sender.sendMessage("§aOcorreu um erro ao remover o local de origem.");
    }
  }

  public String hex(String message) {
    Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    Matcher matcher = pattern.matcher(message);
    while (matcher.find()) {
      String hexCode = message.substring(matcher.start(), matcher.end());
      String replaceSharp = hexCode.replace('#', 'x');

      char[] ch = replaceSharp.toCharArray();
      StringBuilder builder = new StringBuilder();
      for (char c : ch) {
        builder.append("&").append(c);
      }
      message = message.replace(hexCode, builder.toString());
      matcher = pattern.matcher(message);
    }
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  // Função para ajustar o yaw do jogador para a direção cardinal mais próxima
  public static float getCardinalYaw(float currentYaw) {
    // Ajustar o yaw para a direção cardinal mais próxima
    if (currentYaw >= -45 && currentYaw < 45) {
      return 0; // Sul
    } else if (currentYaw >= 45 && currentYaw < 135) {
      return 90; // Oeste
    } else if (currentYaw >= -135 && currentYaw < -45) {
      return -90; // Leste
    } else {
      return -180; // Norte
    }
  }

  public void launchRandomFirework(Player player) {
    Firework firework = (Firework) Objects.requireNonNull(player.getWorld()).spawnEntity(player.getLocation(), EntityType.FIREWORK);
    FireworkMeta fireworkMeta = firework.getFireworkMeta();
    Random random = new Random();
    FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(Objects.requireNonNull(getColor(random.nextInt(17) + 1))).withFade(Objects.requireNonNull(getColor(random.nextInt(17) + 1))).with(FireworkEffect.Type.values()[random.nextInt((FireworkEffect.Type.values()).length)]).trail(random.nextBoolean()).build();
    fireworkMeta.addEffect(effect);
    fireworkMeta.setPower(random.nextInt(2) + 1);
    firework.setFireworkMeta(fireworkMeta);
  }

  private Color getColor(int i) {
    return switch (i) {
      case 1 -> Color.AQUA;
      case 2 -> Color.BLACK;
      case 3 -> Color.BLUE;
      case 4 -> Color.FUCHSIA;
      case 5 -> Color.GRAY;
      case 6 -> Color.GREEN;
      case 7 -> Color.LIME;
      case 8 -> Color.MAROON;
      case 9 -> Color.NAVY;
      case 10 -> Color.OLIVE;
      case 11 -> Color.ORANGE;
      case 12 -> Color.PURPLE;
      case 13 -> Color.RED;
      case 14 -> Color.SILVER;
      case 15 -> Color.TEAL;
      case 16 -> Color.WHITE;
      case 17 -> Color.YELLOW;
      default -> null;
    };
  }
}
