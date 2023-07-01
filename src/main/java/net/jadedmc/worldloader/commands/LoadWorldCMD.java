package net.jadedmc.worldloader.commands;

import net.jadedmc.worldloader.WorldLoaderPlugin;
import net.jadedmc.worldloader.miniworld.MiniWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoadWorldCMD implements CommandExecutor {
    private final WorldLoaderPlugin plugin;

    public LoadWorldCMD(WorldLoaderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!(sender instanceof Player player)) {
            return true;
        }

        if(args.length == 0) {
            return true;
        }

        String map = args[0];

        MiniWorld miniWorld = plugin.miniWorldManager().createMiniWorld(map, map);

        player.sendMessage(ChatColor.GREEN + "Loading world...");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
           while (!miniWorld.loaded()) {
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }

           plugin.getServer().getScheduler().runTask(plugin, () -> {
               player.sendMessage(ChatColor.GREEN + "World loaded!");
               player.teleport(miniWorld.world().getSpawnLocation());
           });
        });

        return true;
    }

}
