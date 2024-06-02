package origins.spawn.utils;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

  public HashMap<UUID, Location> cooldowns = new HashMap<>();

  public void setCooldown(UUID uuid, Location location) {
    cooldowns.put(uuid, location);
  }

  public void removeCooldown(UUID uuid) {
    cooldowns.remove(uuid);
  }

  public boolean hasCooldown(UUID uuid) {
    return cooldowns.containsKey(uuid);
  }

  public Location getCooldown(UUID uuid) {
    return cooldowns.get(uuid);
  }
}
