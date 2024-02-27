package me.fodded.skywars.listeners;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.skywars.managers.LobbyPlayer;
import me.fodded.spigotcore.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onJoinAsync(AsyncPlayerPreLoginEvent event) {

    }

    @EventHandler
    public void onJoinSync(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player.getUniqueId());

        CompletableFuture<Void> loadStatistics = CompletableFuture.runAsync(() -> lobbyPlayer.loadDataAsync(player));
        loadStatistics.thenRun(() -> {
            Bukkit.getScheduler().runTask(SpigotCore.getInstance().getPlugin(), lobbyPlayer::handleJoin);
        });
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        // We allow it to all lobby servers if the player has needed rank
        // We allow it to all minigame servers regardless of player's rank
        if (!event.getResult().equals(PlayerLoginEvent.Result.KICK_FULL)) return;
        if (!SpigotCore.getInstance().getServerName().contains("Lobby")) {
            event.allow();
            return;
        }

        if(Rank.hasPermission(Rank.HELPER, event.getPlayer().getUniqueId())) {
            event.allow();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        LobbyPlayer lobbyPlayer = LobbyPlayer.getLobbyPlayer(event.getPlayer().getUniqueId());
        lobbyPlayer.handleQuit();
    }
}
