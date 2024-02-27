package me.fodded.spigotcore.servers.tasks;

import me.fodded.spigotcore.servers.SpigotServerManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerPlayersAmountTask extends BukkitRunnable {

    public ServerPlayersAmountTask() {

    }

    @Override
    public void run() {
        SpigotServerManager.getInstance().updatePlayerCount(Bukkit.getOnlinePlayers().size());
    }
}
