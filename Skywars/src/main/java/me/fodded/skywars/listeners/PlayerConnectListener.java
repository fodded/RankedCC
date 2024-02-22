package me.fodded.skywars.listeners;

import me.fodded.skywars.Main;
import me.fodded.skywars.gameplay.scoreboard.SkywarsLobbyScoreboard;
import me.fodded.skywars.managers.LobbyPlayer;
import me.fodded.skywars.managers.ServerLocations;
import me.fodded.skywars.tasks.UpdateScoreboardTask;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.servers.SpigotServerManager;
import org.bukkit.Bukkit;
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
        LobbyPlayer lobbyPlayer = new LobbyPlayer(event.getPlayer().getUniqueId());
        lobbyPlayer.handleJoin();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        LobbyPlayer lobbyPlayer = LobbyPlayer.getLobbyPlayer(event.getPlayer().getUniqueId());
        lobbyPlayer.handleQuit();
    }
}
