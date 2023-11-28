package origins.spawn.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import origins.spawn.commands.CommandLocation;
import origins.spawn.events.EventsManager;
import origins.spawn.utils.FunctionsManager;

import java.util.HashMap;
import java.util.UUID;

public final class MainSpawn extends JavaPlugin {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static MainSpawn instance;

    private FunctionsManager functions;

    public HashMap<UUID, Location> teleportCooldown = new HashMap<>();

    public static MainSpawn getPlugin() {
        return instance;
    }

    public void registerCommands() {
        new CommandLocation(this, "inicio");
        console.sendMessage("§a" + getPlugin().getName() + ": Comandos carregados com sucesso.");
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new EventsManager(), this);
        console.sendMessage("§a" + getPlugin().getName() + ": Eventos carregados com sucesso.");
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        functions = new FunctionsManager();
        registerCommands();
        registerEvents();
        console.sendMessage("§a" + getPlugin().getName() + ": Plugin habilitado com sucesso.");
    }

    @Override
    public void onDisable() {
        saveConfig();
        console.sendMessage("§c" + getPlugin().getName() + ": Plugin desabilitado com sucesso.");
    }

    public FunctionsManager getFunctions() {
        return functions;
    }
}
