package net.jadedmc.worldloader;

import net.jadedmc.worldloader.commands.LoadWorldCMD;
import net.jadedmc.worldloader.miniworld.MiniWorld;
import net.jadedmc.worldloader.miniworld.MiniWorldManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldLoaderPlugin extends JavaPlugin implements CommandExecutor, Listener {
    private SettingsManager settingsManager;
    private MiniWorldManager miniWorldManager;
    private MySQL mySQL;

    @Override
    public void onEnable() {
        // Plugin startup logic
        settingsManager = new SettingsManager(this);
        mySQL = new MySQL(this);
        miniWorldManager = new MiniWorldManager(this);

        getCommand("loadworld").setExecutor(new LoadWorldCMD(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        for(MiniWorld miniWorld : miniWorldManager.miniWorlds()) {
            miniWorld.delete();
        }
    }

    public SettingsManager settingsManager() {
        return settingsManager;
    }

    public MiniWorldManager miniWorldManager() {
        return miniWorldManager;
    }

    public MySQL mySQL() {
        return mySQL;
    }
}
