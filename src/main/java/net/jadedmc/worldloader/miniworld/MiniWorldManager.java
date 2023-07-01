package net.jadedmc.worldloader.miniworld;

import net.jadedmc.worldloader.WorldLoaderPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MiniWorldManager {
    private final WorldLoaderPlugin plugin;
    private final Map<String, MiniWorld> miniWorlds = new HashMap<>();

    public MiniWorldManager(WorldLoaderPlugin plugin) {
        this.plugin = plugin;
    }

    public MiniWorld createMiniWorld(String worldName, String mapName) {
        return new MiniWorld(plugin, worldName, mapName);
    }

    public void deleteMiniWorld(MiniWorld miniWorld) {
        miniWorlds.remove(miniWorld.world().getName());
    }

    public Collection<MiniWorld> miniWorlds() {
        return new HashSet<>(miniWorlds.values());
    }
}