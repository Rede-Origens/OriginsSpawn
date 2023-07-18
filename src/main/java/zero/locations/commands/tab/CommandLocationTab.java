package zero.locations.commands.tab;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import zero.locations.main.MainLocations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandLocationTab implements TabCompleter {

  private final List<String> args1 = Collections.singletonList("teleportar");
  private final List<String> args3 = Arrays.asList("ativado", "desativado");

  private final List<String> args1Admin = Arrays.asList("definir", "remover", "forçar", "recarregar");

  private final ArrayList<String> locations = new ArrayList<>();
  private final ArrayList<String> players = new ArrayList<>();

  @Override
  public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {

    final List<String> completions = new ArrayList<>();

    if (!(sender instanceof Player)) {
      return completions;
    }

    switch (args.length) {
      case 1: {
        if (sender.hasPermission("locations.admin")) {
          StringUtil.copyPartialMatches(args[0], args1Admin, completions);
        }
        StringUtil.copyPartialMatches(args[0], args1, completions);
        break;
      }
      case 2: {
        switch (args[0]) {
          case "forçar":
          case "remover":
          case "teleportar": {
            ConfigurationSection section = MainLocations.getPlugin().getConfig().getConfigurationSection("locations");
            locations.clear();

            if (section != null) {
              for (String loc : section.getKeys(false)) {
                if (sender.hasPermission("locations." + loc)) {
                  locations.add(loc);
                }
              }
            }
            StringUtil.copyPartialMatches(args[1], locations, completions);
          }
        }
        break;
      }
      case 3: {
        switch (args[0]) {
          case "forçar": {
            players.clear();
            Bukkit.getOnlinePlayers().forEach(p -> {
              players.add(p.getName());
            });
            StringUtil.copyPartialMatches(args[2], players, completions);
            break;
          }
          case "definir": {
            if (sender.hasPermission("locations.admin")) {
              StringUtil.copyPartialMatches(args[2], args3, completions);
            }
            break;
          }
        }
        break;
      }
    }
    Collections.sort(completions);
    return completions;
  }
}
