package origins.spawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import origins.spawn.commands.tab.CommandLocationTab;
import origins.spawn.main.OriginsSpawn;
import origins.spawn.utils.LocationManager;

import java.util.Objects;

public class CommandLocation implements CommandExecutor {

  private final OriginsSpawn plugin;
  private final LocationManager locationManager;

  public CommandLocation(OriginsSpawn plugin, String command) {
    Objects.requireNonNull(plugin.getCommand(command)).setExecutor(this);
    Objects.requireNonNull(plugin.getCommand(command)).setTabCompleter(new CommandLocationTab());
    this.plugin = plugin;
    this.locationManager = plugin.getLocationManager();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command arg1, @NotNull String arg2, String[] args) {
    switch (args.length) {
      case 0 -> {
        args0(sender);
        return true;
      }
      case 1 -> {
        args1(sender, args);
        return true;
      }
      case 2 -> {
        args2(sender, args);
        return true;
      }
      default -> {
        sender.sendMessage("§cArgumentos inválidos.");
        return true;
      }
    }
  }

  private void args0(CommandSender sender) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("§cVocê nâo pode usar este comando no console.");
      return;
    }
    locationManager.teleportSpawn(player);
  }


  private void args1(CommandSender sender, String[] args) {
    switch (args[0]) {
      case "recarregar" -> {
        if (!sender.hasPermission("origins.spawn.reload")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        try {
          plugin.reloadConfig();
          sender.sendMessage("§aConfigurações reiniciadas com sucesso.");
        } catch (Exception error) {
          sender.sendMessage("§cOcorreu um erro ao reiniciar as configurações.");
        }
      }
      case "definir" -> {
        if (!sender.hasPermission("origins.spawn.set")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        locationManager.setSpawn((Player) sender);
      }
      case "remover" -> {
        if (!sender.hasPermission("origins.spawn.unset")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        locationManager.deleteSpawn(sender);
      }
      default -> sender.sendMessage("§cArgumentos inválidos.");
    }
  }

  private void args2(CommandSender sender, String[] args) {
    if (args[0].equals("forçar")) {
      if (!sender.hasPermission("origins.spawn.force")) {
        sender.sendMessage("§cVocê não tem permissão para fazer isto.");
        return;
      }
      Player player = (Player) sender;
      Player target = Bukkit.getPlayerExact(args[1]);

      if (target == null) {
        player.sendMessage("§cJogador não encontrado.");
        return;
      }
      locationManager.teleportSpawn(target);
    } else {
      sender.sendMessage("§cArgumentos inválidos.");
    }
  }
}
