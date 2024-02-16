package me.fodded.proxycore.listeners;

import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.GeneralStatsDataManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        GeneralStatsDataManager generalStatsDataManager = Core.getInstance().getGeneralStatsDataManager();
        generalStatsDataManager.loadFromDatabaseToRedis(uniqueId);

        generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setLastName(player.getName()));
        generalStatsDataManager.getCachedValue(uniqueId);

        // TODO: Send request to each server being online to load data from database to redis
        // If we have 5 instances of Skywars servers, then we need to get online one (Loop through all sw servers and push data to the first)
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        GeneralStatsDataManager generalStatsDataManager = Core.getInstance().getGeneralStatsDataManager();
        generalStatsDataManager.unloadFromRedisToDatabase(uniqueId);

        generalStatsDataManager.removeFromCache(uniqueId);

        // TODO: Send request to each server being online to load data from redis to database
        // If we have 5 instances of Skywars servers, then we need to get online one (Loop through all sw servers and push data to the first)
    }

}
