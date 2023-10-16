package origins.spawn.events;

import lb.kits.main.MainKits;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;
import origins.spawn.main.MainSpawn;

public class EventsManager implements Listener {

  @EventHandler
  public void onRespawn(PlayerRespawnEvent event) {
    event.setRespawnLocation(MainSpawn.getPlugin().getFunctions().getSpawn());
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!player.hasPlayedBefore()) {
      // Teleportar o jogador para o inicio
      Bukkit.getScheduler().runTaskLater(MainSpawn.getPlugin(), () -> player.teleport(MainSpawn.getPlugin().getFunctions().getSpawn(), PlayerTeleportEvent.TeleportCause.COMMAND), 3L);

      // Sistema de enviar mensagem
      boolean messageStatus = MainSpawn.getPlugin().getConfig().getBoolean("welcomeMessageStatus"); // Estado da função de mensagem de boas vindas.

      String defaultMessage = "§a%player% entrou pela primeira vez no servidor.";
      String message = MainSpawn.getPlugin().getConfig().getString("welcomeMessage");
      message = (message != null) ? message : defaultMessage;

      if (messageStatus) {
        event.setJoinMessage(null);
        Bukkit.broadcastMessage(MainSpawn.getPlugin().getFunctions().hex(message.replaceAll("%player%", player.getName())));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
      }

      // Sistema de dar o conjunto
      boolean kitStatus = MainSpawn.getPlugin().getConfig().getBoolean("kitStatus");
      String kitName = MainSpawn.getPlugin().getConfig().getString("kitName");

      if (kitStatus && kitName != null) {
        MainKits.getPlugin().getFunctions().giveKit(player, kitName, false);
      }
    } else {
      boolean joinMessageStatus = MainSpawn.getPlugin().getConfig().getBoolean("joinMessageStatus");

      String defaultMessage = "§a%player% entrou no servidor";
      String joinMessage = MainSpawn.getPlugin().getConfig().getString("joinMessage");
      joinMessage = (joinMessage != null) ? joinMessage : defaultMessage;

      if (joinMessageStatus) {
        event.setJoinMessage(MainSpawn.getPlugin().getFunctions().hex(joinMessage.replaceAll("%player%", player.getName())));
      } else {
        event.setJoinMessage(null);
      }
    }
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    boolean quitMessageStatus = MainSpawn.getPlugin().getConfig().getBoolean("quitMessageStatus");

    String defaultMessage = "§c%player% saiu do servidor.";
    String quitMessage = MainSpawn.getPlugin().getConfig().getString("quitMessage");
    quitMessage = (quitMessage != null) ? quitMessage : defaultMessage;

    if (quitMessageStatus)
      event.setQuitMessage(MainSpawn.getPlugin().getFunctions().hex(quitMessage.replaceAll("%player%", player.getName())));
    else event.setQuitMessage(null);
  }
}
