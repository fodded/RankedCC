package me.fodded.rankedskywars.gameplay.listeners;

import me.fodded.rankedskywars.managers.RankedSkywarsPlayer;
import me.fodded.spigotcore.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onJoinAsync(AsyncPlayerPreLoginEvent event) {

    }

    @EventHandler
    public void onJoinSync(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RankedSkywarsPlayer lobbyPlayer = new RankedSkywarsPlayer(player.getUniqueId());

        CompletableFuture<Void> loadStatistics = CompletableFuture.runAsync(() -> lobbyPlayer.loadDataAsync(player));
        loadStatistics.thenRun(() -> {
            Bukkit.getScheduler().runTask(SpigotCore.getInstance().getPlugin(), lobbyPlayer::handleJoin);
            lobbyPlayer.tellBungeePlayerJoined();
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        RankedSkywarsPlayer lobbyPlayer = RankedSkywarsPlayer.getLobbyPlayer(event.getPlayer().getUniqueId());
        lobbyPlayer.handleQuit();
    }
}