package me.fodded.spigotcore.tasks;

import me.fodded.spigotcore.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class KeepDayTask extends BukkitRunnable {

    public KeepDayTask() {

    }

    @Override
    public void run() {
        JavaPlugin plugin = SpigotCore.getInstance().getPlugin();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for(World world : plugin.getServer().getWorlds()) {
                world.setTime(6000L);
            }
        });
    }
}
