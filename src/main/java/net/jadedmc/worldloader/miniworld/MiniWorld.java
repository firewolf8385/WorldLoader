package net.jadedmc.worldloader.miniworld;

import net.jadedmc.worldloader.EmptyChunkGenerator;
import net.jadedmc.worldloader.WorldLoaderPlugin;
import net.jadedmc.worldloader.utils.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MiniWorld {
    private final WorldLoaderPlugin plugin;
    private World world;
    private final String mapName;
    private boolean loaded;

    public MiniWorld(WorldLoaderPlugin plugin, String worldName, String mapName) {
        this.plugin = plugin;
        this.mapName = mapName;
        this.loaded = false;

        // Generates the world files.
        WorldCreator tempWorldCreator = new WorldCreator(worldName);
        tempWorldCreator.generator(new EmptyChunkGenerator());
        World tempWorld = tempWorldCreator.createWorld();
        assert tempWorld != null;
        Bukkit.unloadWorld(tempWorld, false);

        // Get the map from MySQL.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            // Keep trying until a connection is made.
            while(plugin.mySQL().getConnection() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM maps WHERE mapName = ? LIMIT 1");
                statement.setString(1, mapName);
                ResultSet resultSet = statement.executeQuery();

                // If it finds the map, create the world.
                if(resultSet.next()) {
                    // Get the region file from MySQL.
                    Blob blob = resultSet.getBlob("mapFile");
                    InputStream inputStream = blob.getBinaryStream();

                    // Save the file to the server.
                    File regionFolder = new File(tempWorld.getWorldFolder(), "region");
                    File regionFile = new File(regionFolder, "r.0.0.mca");
                    Files.copy(inputStream, regionFile.toPath());

                    // Create and load the world.
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        // Create the world.
                        WorldCreator worldCreator = new WorldCreator(worldName);
                        worldCreator.generator(new EmptyChunkGenerator());
                        world = worldCreator.createWorld();

                        // Set gamerules.
                        assert world != null;
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);

                        // Set time and weather.
                        world.setStorm(false);
                        world.setTime(6000);

                        // Set world border.
                        WorldBorder worldBorder = world.getWorldBorder();
                        worldBorder.setCenter(new Location(world, 256, 100, 256));
                        worldBorder.setSize(512);

                        this.loaded = true;
                    });
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void delete() {
        Bukkit.unloadWorld(world, false);
        FileUtils.deleteDirectory(world.getWorldFolder());
        loaded = false;
        plugin.miniWorldManager().deleteMiniWorld(this);
    }

    public boolean loaded() {
        return loaded;
    }

    public String mapName() {
        return mapName;
    }

    public World world() {
        return world;
    }
}
