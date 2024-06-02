package origins.spawn.main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import origins.spawn.commands.CommandLocation;
import origins.spawn.events.EventsManager;
import origins.spawn.utils.CooldownManager;
import origins.spawn.utils.LocationManager;

public final class OriginsSpawn extends JavaPlugin {

  private final ConsoleCommandSender console = Bukkit.getConsoleSender();

  private static OriginsSpawn instance;

  private LocationManager locationManager;
  private CooldownManager cooldownManager;

  public static OriginsSpawn getPlugin() {
    return instance;
  }

  public void registerCommands() {
    new CommandLocation(this, "inicio");
    console.sendMessage("§a" + getPlugin().getName() + ": Comandos carregados com sucesso.");
  }

  public void registerEvents() {
    getServer().getPluginManager().registerEvents(new EventsManager(this), this);
    console.sendMessage("§a" + getPlugin().getName() + ": Eventos carregados com sucesso.");
  }

  @Override
  public void onEnable() {
    instance = this;
    cooldownManager = new CooldownManager();
    locationManager = new LocationManager(this);
    registerCommands();
    registerEvents();
    console.sendMessage("§a" + getPlugin().getName() + ": Plugin habilitado com sucesso.");
  }

  @Override
  public void onDisable() {
    saveConfig();
    console.sendMessage("§c" + getPlugin().getName() + ": Plugin desabilitado com sucesso.");
  }

  public LocationManager getLocationManager() {
    return locationManager;
  }

  public CooldownManager getCooldownManager() {
    return cooldownManager;
  }
}
