package zero.locations.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import zero.locations.main.MainLocations;

import java.util.Objects;

public class FunctionsManager {

  public FileConfiguration getConfig() {
    return MainLocations.getPlugin().getConfig();
  }

  public void setLocation(Player player, String location, Boolean onlyInGui) {
    if (getConfig().getConfigurationSection("locations." + location) != null) {
      player.sendMessage("§cUm local com esté nome já foi definido.");
      return;
    }
    createLocation(player, location, onlyInGui);
  }

  public void teleportToLocation(Player player, String location) {
    if (getConfig().getConfigurationSection("locations." + location) == null) {
      player.sendMessage("§cLocal não encontrado.");
      return;
    }
    boolean onlyInGui = getConfig().getBoolean("locations." + location + ".onlyInGui");

    if (onlyInGui) {
      if (!player.getOpenInventory().getTitle().equalsIgnoreCase("Locais")) {
        player.sendMessage("§cLocal não encontrado.");
        return;
      }
    }
    try {
      player.teleport(getLocation(location));
      player.sendMessage("§aTeletransportado para " + location + ".");
      player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    } catch (NullPointerException exception) {
      player.sendMessage("§cOcorreu um erro ao teletransportar para " + location + ".");
    }
  }

  public Location getLocation(String location) {
    double x = getConfig().getDouble("locations." + location + ".coordinates.x");
    double y = getConfig().getDouble("locations." + location + ".coordinates.y");
    double z = getConfig().getDouble("locations." + location + ".coordinates.z");
    float yaw = (float) getConfig().getDouble("locations." + location + ".coordinates.yaw");
    float pitch = (float) getConfig().getDouble("locations." + location + ".coordinates.pitch");
    String worldName = getConfig().getString("locations." + location + ".coordinates.world");

    assert worldName != null;
    World world = Bukkit.getWorld(worldName);

    if (world == null) {
      return null;
    } else {
      final Location loc = new Location(world, x, y, z);
      loc.setYaw(yaw);
      loc.setPitch(pitch);
      return loc;
    }
  }

  public void createLocation(Player player, String location, Boolean onlyInGui) {
    Location loc = player.getLocation();
    String world = Objects.requireNonNull(loc.getWorld()).getName();
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();
    float yaw = loc.getYaw();
    float pitch = loc.getPitch();

    getConfig().set("locations." + location + ".onlyInGui", onlyInGui);
    getConfig().set("locations." + location + ".coordinates.x", x);
    getConfig().set("locations." + location + ".coordinates.y", y);
    getConfig().set("locations." + location + ".coordinates.z", z);
    getConfig().set("locations." + location + ".coordinates.yaw", yaw);
    getConfig().set("locations." + location + ".coordinates.pitch", pitch);
    getConfig().set("locations." + location + ".coordinates.world", world);

    try {
      MainLocations.getPlugin().saveConfig();
      player.sendMessage("§aLocal " + location + " definido.");
    } catch (Exception exception) {
      player.sendMessage("§cOcorreu um erro ao definir o local.");
    }
  }

  public void deleteLocation(CommandSender sender, String location) {
    if (getConfig().getConfigurationSection("locations." + location) == null) {
      sender.sendMessage("§cLocal não encontrado.");
      return;
    }
    try {
      getConfig().set("locations." + location, null);
      MainLocations.getPlugin().saveConfig();
      sender.sendMessage("§aLocal " + location + " removido.");
    } catch (Exception exception) {
      sender.sendMessage("§aOcorreu um erro ao remover o local.");
    }
  }

  public Object convertBoolean(String options) {
    switch (options) {
      case "ativado": {
        return true;
      }
      case "desativado": {
        return false;
      }
      default: {
        return null;
      }
    }
  }
}
