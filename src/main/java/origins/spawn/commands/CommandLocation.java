package origins.spawn.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import origins.spawn.commands.tab.CommandLocationTab;
import origins.spawn.main.MainSpawn;
import origins.spawn.utils.FunctionsManager;

import java.util.Objects;

public class CommandLocation implements CommandExecutor {

  private final FunctionsManager functions = new FunctionsManager();

  public CommandLocation(MainSpawn locations, String command) {
    Objects.requireNonNull(locations.getCommand(command)).setExecutor(this);
    Objects.requireNonNull(locations.getCommand(command)).setTabCompleter(new CommandLocationTab());
  }

  @Override
  public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
    switch (args.length) {
      case 0 -> {
        args0(sender, args);
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

  private void args0(CommandSender sender, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("§cVocê nâo pode usar este comando no console.");
      return;
    }
    functions.teleportSpawn((Player) sender);
  }


  private void args1(CommandSender sender, String[] args) {
    switch (args[0]) {
      case "recarregar" -> {
        if (!sender.hasPermission("spawn.admin.reload")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        try {
          MainSpawn.getPlugin().reloadConfig();
          sender.sendMessage("§aConfigurações reiniciadas com sucesso.");
        } catch (Exception error) {
          sender.sendMessage("§cOcorreu um erro ao reiniciar as configurações.");
        }
      }
      case "definir" -> {
        if (!sender.hasPermission("spawn.admin.define")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        functions.setSpawn((Player) sender);
      }
      case "remover" -> {
        if (!sender.hasPermission("spawn.admin.remove")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        functions.deleteSpawn(sender);
      }
      default -> sender.sendMessage("§cArgumentos inválidos.");
    }
  }

  private void args2(CommandSender sender, String[] args) {
    switch (args[0]) {
      case "forçar" -> {
        if (!sender.hasPermission("spawn.admin.force")) {
          sender.sendMessage("§cVocê não tem permissão para fazer isto.");
          return;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null) {
          player.sendMessage("§cJogador não encontrado.");
          return;
        }
        functions.teleportSpawn(target);
      }
      default -> sender.sendMessage("§cArgumentos inválidos.");
    }
  }
}
