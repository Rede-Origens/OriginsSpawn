package origins.spawn.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorManager {

  private static final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");

  public static String hex(String message) {
    Matcher matcher = hexPattern.matcher(message);
    StringBuilder buffer = new StringBuilder();

    while (matcher.find()) {
      String hexCode = matcher.group().substring(1);
      StringBuilder replacement = new StringBuilder("&x");

      for (char c : hexCode.toCharArray()) {
        replacement.append("&").append(c);
      }

      matcher.appendReplacement(buffer, replacement.toString());
    }
    matcher.appendTail(buffer);

    return ChatColor.translateAlternateColorCodes('&', buffer.toString());
  }
}
