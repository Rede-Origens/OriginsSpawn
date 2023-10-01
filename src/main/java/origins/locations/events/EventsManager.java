package origins.locations.events;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import origins.locations.main.MainLocations;

public class EventsManager implements Listener {

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    event.setRespawnLocation(MainLocations.getPlugin().getFunctions().getSpawn());
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!player.hasPlayedBefore()) {
      boolean status = MainLocations.getPlugin().getConfig().getBoolean("welcomeMessageStatus");
      String message = MainLocations.getPlugin().getConfig().getString("welcomeMessage");

      if (status && message != null) {
        Bukkit.broadcastMessage(MainLocations.getPlugin().getFunctions().hex(message.replaceAll("%player%", player.getName())));
        player.sendMessage(message);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
      }
    }
  }
}
