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
        Player player = event.getPlayer();
        player.teleport(ServerLocations.getInstance().getLobbyLocation());
        player.setAllowFlight(true); // needed for double jump

        UpdateScoreboardTask updateScoreboardTask = new UpdateScoreboardTask(player);
        updateScoreboardTask.runTaskTimer(SpigotCore.getInstance().getPlugin(), 10L, 20L);

        // telling redis what is actual amount of players on our server atm
        SpigotServerManager.getInstance().updatePlayerCount(Bukkit.getOnlinePlayers().size()+1);

        // We need to give player items with a small delay, so we are sure we have enough time to load data
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if(player.isOnline()) {
                lobbyPlayer.initializePlayer();
            }
        }, 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SkywarsLobbyScoreboard skywarsLobbyScoreboard = (SkywarsLobbyScoreboard) SkywarsLobbyScoreboard.getScoreboardManager(player);
        if(skywarsLobbyScoreboard != null) {
            skywarsLobbyScoreboard.removeScoreboard();
        }

        // telling redis what is actual amount of players on our server atm
        SpigotServerManager.getInstance().updatePlayerCount(Bukkit.getOnlinePlayers().size()-1);
    }
}
