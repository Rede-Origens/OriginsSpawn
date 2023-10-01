package origins.locations.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import origins.locations.main.MainLocations;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionsManager {

  public FileConfiguration getConfig() {
    return MainLocations.getPlugin().getConfig();
  }

  public void teleportSpawn(Player player) {
    if (getSpawn() == null) {
      player.sendMessage("§cLocal de origem não encontrado.");
      return;
    }
    player.teleport(getSpawn());
    player.sendMessage("§aTeletransportado para o local de origem.");
    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
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
      MainLocations.getPlugin().saveConfig();
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
      MainLocations.getPlugin().saveConfig();
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
