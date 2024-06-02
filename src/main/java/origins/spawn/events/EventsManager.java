package origins.spawn.events;

import lb.kits.main.MainKits;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import origins.spawn.main.OriginsSpawn;
import origins.spawn.utils.CooldownManager;
import origins.spawn.utils.LocationManager;

import java.util.HashMap;
import java.util.UUID;

import static origins.spawn.utils.ColorManager.hex;

public class EventsManager implements Listener {

  private final OriginsSpawn plugin;
  private final LocationManager locationManager;
  private final CooldownManager cooldownManager;

  public EventsManager(OriginsSpawn plugin) {
    this.plugin = plugin;
    this.locationManager = plugin.getLocationManager();
    this.cooldownManager = plugin.getCooldownManager();
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    event.setRespawnLocation(locationManager.getSpawn());
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    if (cooldownManager.hasCooldown(player.getUniqueId())) {
      Location initialLocation = cooldownManager.getCooldown(player.getUniqueId());
      Location currentLocation = event.getTo();

      if (currentLocation != null) {
        double initialX = initialLocation.getX();
        double initialY = initialLocation.getY();
        double initialZ = initialLocation.getZ();

        double currentX = currentLocation.getX();
        double currentY = currentLocation.getY();
        double currentZ = currentLocation.getZ();

        if (initialX != currentX || initialY != currentY || initialZ != currentZ) {
          player.sendMessage("§cTeletransporte cancelado.");
          player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
          cooldownManager.removeCooldown(player.getUniqueId());
        }
      }
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!player.hasPlayedBefore()) {
      // Teleportar o jogador para o inicio
      Bukkit.getScheduler().runTaskLater(OriginsSpawn.getPlugin(), () -> player.teleport(locationManager.getSpawn(), PlayerTeleportEvent.TeleportCause.COMMAND), 3L);

      // Sistema de enviar mensagem
      boolean messageStatus = OriginsSpawn.getPlugin().getConfig().getBoolean("welcomeMessageStatus"); // Estado da função de mensagem de boas vindas.

      String defaultMessage = "§a%player% entrou pela primeira vez no servidor.";
      String message = OriginsSpawn.getPlugin().getConfig().getString("welcomeMessage");
      message = (message != null) ? message : defaultMessage;

      if (messageStatus) {
        event.setJoinMessage(null);
        Bukkit.broadcastMessage(hex(message.replaceAll("%player%", player.getName())));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
      }

      // Sistema de dar o conjunto
      boolean kitStatus = OriginsSpawn.getPlugin().getConfig().getBoolean("kitStatus");
      String kitName = OriginsSpawn.getPlugin().getConfig().getString("kitName");

      if (kitStatus && kitName != null) {
        MainKits.getPlugin().getFunctions().giveKit(player, kitName, false);
      }

      boolean fireworkStatus = OriginsSpawn.getPlugin().getConfig().getBoolean("fireworkStatus");

      if (fireworkStatus) {
        locationManager.launchRandomFirework(player);
      }
    } else {
      boolean joinMessageStatus = OriginsSpawn.getPlugin().getConfig().getBoolean("joinMessageStatus");

      String defaultMessage = "§a%player% entrou no servidor";
      String joinMessage = OriginsSpawn.getPlugin().getConfig().getString("joinMessage");
      joinMessage = (joinMessage != null) ? joinMessage : defaultMessage;

      if (joinMessageStatus) {
        event.setJoinMessage(hex(joinMessage.replaceAll("%player%", player.getName())));
      } else {
        event.setJoinMessage(null);
      }
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    // Remover o jogador do teletransporte caso esteja
    cooldownManager.removeCooldown(player.getUniqueId());

    boolean quitMessageStatus = OriginsSpawn.getPlugin().getConfig().getBoolean("quitMessageStatus");

    String defaultMessage = "§c%player% saiu do servidor.";
    String quitMessage = OriginsSpawn.getPlugin().getConfig().getString("quitMessage");
    quitMessage = (quitMessage != null) ? quitMessage : defaultMessage;

    if (quitMessageStatus) {
      event.setQuitMessage(hex(quitMessage.replaceAll("%player%", player.getName())));
    } else {
      event.setQuitMessage(null);
    }
  }
}
