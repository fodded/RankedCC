package me.fodded.spigotcore.listeners;

import me.fodded.core.model.DataManager;
import me.fodded.core.model.GlobalDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        for(GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
            dataManager.removeFromCache(uniqueId);
        }
    }
}
