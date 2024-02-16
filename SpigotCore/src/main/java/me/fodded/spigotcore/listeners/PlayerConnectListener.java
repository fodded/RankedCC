package me.fodded.spigotcore.listeners;

import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.GeneralStatsDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        GeneralStatsDataManager generalStatsDataManager = Core.getInstance().getGeneralStatsDataManager();
        generalStatsDataManager.getCachedValue(uniqueId);

        player.sendMessage(generalStatsDataManager.getCachedValue(uniqueId).getPrefix() + " prefix");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        GeneralStatsDataManager generalStatsDataManager = Core.getInstance().getGeneralStatsDataManager();
        generalStatsDataManager.removeFromCache(uniqueId);
    }
}
