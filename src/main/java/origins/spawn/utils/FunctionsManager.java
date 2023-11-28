package origins.spawn.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import origins.spawn.main.MainSpawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionsManager {

  public FileConfiguration getConfig() {
    return MainSpawn.getPlugin().getConfig();
  }

  public void teleportSpawn(Player player) {
    if (getSpawn() == null) {
      player.sendMessage("§cLocal de origem não encontrado.");
      return;
    }

    UUID uuid = player.getUniqueId();
    HashMap<UUID, Location> cooldowns = MainSpawn.getPlugin().teleportCooldown;

    if (cooldowns.containsKey(player.getUniqueId())) {
      player.sendMessage("§cVocê já está em um teleporte.");
      return;
    }

    if(player.hasPermission("origins.spawn.bypass")) {
      player.teleport(getSpawn());
      player.sendMessage("§aTeletransportado para o inicio.");
      player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
      return;
    }

    cooldowns.put(uuid, player.getLocation());
    new BukkitRunnable() {
      int countdown = 3;

      @Override
      public void run() {
        if (cooldowns.containsKey(uuid)) {
          if (countdown > 0) {
            player.sendMessage("§aTeletransportando em " + countdown + " segundo(s). Por favor, não se mova.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
            countdown--;
          } else {
            player.teleport(getSpawn());
            player.sendMessage("§aTeletransportado para o inicio.");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            cooldowns.remove(uuid);
            cancel();
          }
        } else {
          cooldowns.remove(uuid);
          cancel();
        }
      }
    }.runTaskTimer(MainSpawn.getPlugin(), 0L, 20);
  }

  public Location getSpawn() {
    double x = getConfig().getDouble("spawn.coordinates.x");
    double y = getConfig().getDouble("spawn.coordinates.y");
    double z = getConfig().getDouble("spawn.coordinates.z");
    float yaw = (float) getConfig().getDouble("spawn.coordinates.yaw");
    float pitch = (float) getConfig().getDouble("spawn.coordinates.pitch");
    String worldName = getConfig().getString("spawn.coordinates.world");
    World world = Bukkit.getWorld(Objects.requireNonNull(worldName));

    try {
      final Location loc = new Location(world, x, y, z);
      loc.setYaw(yaw);
      loc.setPitch(pitch);
      return loc;
    } catch (NullPointerException exception) {
      return null;
    }
  }

  public void setSpawn(Player player) {
    Location loc = player.getLocation();
    String world = Objects.requireNonNull(loc.getWorld()).getName();
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();
    float yaw = loc.getYaw();
    float pitch = loc.getPitch();

    getConfig().set("spawn.coordinates.x", x);
    getConfig().set("spawn.coordinates.y", y);
    getConfig().set("spawn.coordinates.z", z);
    getConfig().set("spawn.coordinates.yaw", yaw);
    getConfig().set("spawn.coordinates.pitch", pitch);
    getConfig().set("spawn.coordinates.world", world);

    try {
      MainSpawn.getPlugin().saveConfig();
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
      getConfig().set("spawn", null);
      MainSpawn.getPlugin().saveConfig();
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
}
