package origins.spawn.commands.tab;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandLocationTab implements TabCompleter {


  private final List<String> args1Admin = Arrays.asList("definir", "remover", "forçar", "recarregar");

  private final ArrayList<String> players = new ArrayList<>();

  @Override
  public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {

    final List<String> completions = new ArrayList<>();

    if (!(sender instanceof Player)) {
      return completions;
    }

    switch (args.length) {
      case 1 -> {
        if (sender.hasPermission("locations.admin")) {
          StringUtil.copyPartialMatches(args[0], args1Admin, completions);
        }
      }
      case 2 -> {
        switch (args[0]) {
          case "forçar": {
            players.clear();
            Bukkit.getOnlinePlayers().forEach(p -> {
              players.add(p.getName());
            });
            StringUtil.copyPartialMatches(args[1], players, completions);
            break;
          }
        }
      }
    }
    Collections.sort(completions);
    return completions;
  }
}
