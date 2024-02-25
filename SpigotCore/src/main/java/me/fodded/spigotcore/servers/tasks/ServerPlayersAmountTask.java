package me.fodded.spigotcore.servers.tasks;

import me.fodded.spigotcore.servers.SpigotServerManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerPlayersAmountTask extends BukkitRunnable {

    private final String serverName;
    public ServerPlayersAmountTask(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void run() {
        SpigotServerManager.getInstance().updatePlayerCount(Bukkit.getOnlinePlayers().size());
    }
}
