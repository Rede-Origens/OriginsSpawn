package zero.locations.main;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import zero.locations.commands.CommandLocation;
import zero.locations.utils.FunctionsManager;

public final class MainLocations extends JavaPlugin {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static MainLocations instance;

    private FunctionsManager functions;

    public static MainLocations getPlugin() {
        return instance;
    }

    public void registerCommands() {
        CommandLocation warp = new CommandLocation(this, "local");
        console.sendMessage("§a" + getPlugin().getName() + ": Comandos carregados com sucesso.");
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        functions = new FunctionsManager();
        registerCommands();
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
