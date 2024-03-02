package me.fodded.spigotcore.listeners;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.model.DataManager;
import me.fodded.core.model.GlobalDataManager;
import me.fodded.spigotcore.SpigotCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID uniqueId = event.getUniqueId();

        //GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        //generalStatsDataManager.getCachedValue(uniqueId);
    }

    @EventHandler
    public void onPlayerJoinSync(PlayerJoinEvent event) {
        event.setJoinMessage(null);
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        event.setQuitMessage(null);
        for(GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
            dataManager.removeFromCache(uniqueId);
        }
    }
}
