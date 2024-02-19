package me.fodded.skywars.listeners;

import me.fodded.skywars.managers.SkywarsLobbyScoreboard;
import me.fodded.skywars.tasks.UpdateScoreboardTask;
import me.fodded.spigotcore.SpigotCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onJoinAsync(AsyncPlayerPreLoginEvent event) {

    }

    @EventHandler
    public void onJoinSync(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UpdateScoreboardTask updateScoreboardTask = new UpdateScoreboardTask(player);
        updateScoreboardTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 10L, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SkywarsLobbyScoreboard skywarsLobbyScoreboard = (SkywarsLobbyScoreboard) SkywarsLobbyScoreboard.getScoreboardManager(player);
        if(skywarsLobbyScoreboard != null) {
            skywarsLobbyScoreboard.removeScoreboard();
        }
    }
}
