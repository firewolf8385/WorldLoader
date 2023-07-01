package net.jadedmc.worldloader;

import net.jadedmc.worldloader.commands.LoadWorldCMD;
import net.jadedmc.worldloader.miniworld.MiniWorld;
import net.jadedmc.worldloader.miniworld.MiniWorldManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        /*
        getCommand("saveworld").setExecutor(this);

        // Load temporary test world.
        WorldCreator tempWorldCreator = new WorldCreator("test");
        tempWorldCreator.generator(new EmptyChunkGenerator());
        World tempWorld = tempWorldCreator.createWorld();
        Bukkit.unloadWorld(tempWorld, false);

        getServer().getScheduler().runTaskAsynchronously(this, () -> {

            while(mySQL.getConnection() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM maps WHERE mapName = ? LIMIT 1");
                statement.setString(1, "hub_island");
                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()) {
                    Blob blob = resultSet.getBlob("mapFile");
                    InputStream inputStream = blob.getBinaryStream();

                    File regionFolder = new File(tempWorld.getWorldFolder(), "region");
                    File regionFile = new File(regionFolder, "r.0.0.mca");
                    Files.copy(inputStream, regionFile.toPath());

                    getServer().getScheduler().runTask(this, () -> {
                        WorldCreator wc2 = new WorldCreator("test");
                        wc2.generator(new EmptyChunkGenerator());
                        World world2 = wc2.createWorld();
                        world2.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world2.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world2.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                        world2.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
                        world2.setStorm(false);
                        world2.setTime(6000);

                        WorldBorder worldBorder = world2.getWorldBorder();
                        worldBorder.setCenter(new Location(world2, 256, 100, 256));
                        worldBorder.setSize(512);
                    });
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }

        });

        getServer().getPluginManager().registerEvents(this, this);

         */
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        /*
        World world = Bukkit.getWorld("test");

        if(world == null) {
            return;
        }

        Bukkit.unloadWorld(world, false);
        deleteDirectory(world.getWorldFolder());

         */

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

    /*
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(label.equalsIgnoreCase("saveworld")) {
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                File regionFile = new File(getDataFolder(), "r.0.0.mca");
                try {
                    FileInputStream input = new FileInputStream(regionFile);

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("REPLACE INTO maps (mapName,mapFile) VALUES (?,?)");
                    statement.setString(1, "hub_island");
                    statement.setBinaryStream(2, input);
                    statement.executeUpdate();

                } catch (FileNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        return true;
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                } //end else
            }
        }
        return( path.delete() );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        World world = Bukkit.getWorld("test");
        event.getPlayer().teleport(world.getSpawnLocation());
    }

     */
}
