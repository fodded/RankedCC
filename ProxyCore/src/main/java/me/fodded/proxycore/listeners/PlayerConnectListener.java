package me.fodded.proxycore.listeners;

import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.model.DataManager;
import me.fodded.core.model.GlobalDataManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ForkJoinPool.commonPool().submit(() -> {
            ProxiedPlayer player = event.getPlayer();
            UUID uniqueId = player.getUniqueId();

            for (GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
                dataManager.loadFromDatabaseToRedis(uniqueId);

                // we need to update the last name all the time player joins
                if (dataManager instanceof GeneralStatsDataManager) {
                    GeneralStatsDataManager generalStatsDataManager = (GeneralStatsDataManager) dataManager;
                    generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setLastName(player.getName()));
                    generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setLastLogin(System.currentTimeMillis()));
                }

                dataManager.getCachedValue(uniqueId);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        ForkJoinPool.commonPool().submit(() -> {
            ProxiedPlayer player = event.getPlayer();
            UUID uniqueId = player.getUniqueId();

            for (GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
                if (dataManager instanceof GeneralStatsDataManager) {
                    GeneralStatsDataManager generalStatsDataManager = (GeneralStatsDataManager) dataManager;
                    generalStatsDataManager.applyChangeToRedis(
                            uniqueId,
                            generalStats -> generalStats.setTimePlayed(generalStats.getTimePlayed() + System.currentTimeMillis() - generalStats.getLastLogin())
                    );
                }

                dataManager.unloadFromRedisToDatabase(uniqueId);
                dataManager.removeFromCache(uniqueId);
            }
        });
    }
}
