package zero.locations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import zero.locations.commands.tab.CommandLocationTab;
import zero.locations.main.MainLocations;
import zero.locations.utils.FunctionsManager;

import java.util.Objects;

public class CommandLocation implements CommandExecutor {

  private final FunctionsManager functions = new FunctionsManager();

  public CommandLocation(MainLocations locations, String command) {
    Objects.requireNonNull(locations.getCommand(command)).setExecutor(this);
    Objects.requireNonNull(locations.getCommand(command)).setTabCompleter(new CommandLocationTab());
  }

  @Override
  public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
    if (!sender.hasPermission("essentials.admin.color")) {
      sender.sendMessage("§cVocê não tem permissão para fazer isto.");
      return true;
    }
    switch (args.length) {
      case 1: {
        args1(sender, args);
        return true;
      }
      case 2: {
        args2(sender, args);
        return true;
      }
      case 3: {
        args3(sender, args);
        return true;
      }
      default: {
        sender.sendMessage("§cArgumentos inválidos.");
        return true;
      }
    }
  }

  private void args1(CommandSender sender, String[] args) {
    switch (args[0]) {
      case "recarregar": {
        if (!sender.hasPermission("kits.admin.reload")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        try {
          MainLocations.getPlugin().reloadConfig();
          sender.sendMessage("§aConfigurações reiniciadas com sucesso.");
        } catch (Exception error) {
          sender.sendMessage("§cOcorreu um erro ao reiniciar as configurações.");
        }
      }
    }
  }

  private void args2(CommandSender sender, String[] args) {
    switch (args[0]) {
      case "remover": {
        String location = args[1];

        if (!sender.hasPermission("locations.remove." + location)) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        functions.deleteLocation(sender, location);
        break;
      }
      case "teletransportar": {
        if (!(sender instanceof Player)) {
          sender.sendMessage("§cVocê nâo pode usar este comando no console.");
          return;
        }
        Player player = (Player) sender;
        String location = args[1];

        if (!player.hasPermission("locations.teleport." + location)) {
          player.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        functions.teleportToLocation(player, location);
        break;
      }
      default: {
        sender.sendMessage("§cArgumentos inválidos.");
        break;
      }
    }
  }

  private void args3(CommandSender sender, String[] args) {
    switch (args[0]) {
      case "definir": {
        if (!(sender instanceof Player)) {
          sender.sendMessage("§cVocê nâo pode usar este comando no console.");
          return;
        }
        Player player = (Player) sender;
        String location = args[1];
        Boolean options = (Boolean) functions.convertBoolean(args[2]);

        if (!player.hasPermission("locations.admin.set")) {
          player.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        if (options == null) {
          player.sendMessage("§cO valor precisa ser ativado ou desativado.");
          return;
        }
        functions.setLocation(player, location, options);
        break;
      }
      default: {
        sender.sendMessage("§cArgumentos inválidos.");
        break;
      }
    }
  }
}
