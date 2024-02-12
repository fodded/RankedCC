package me.fodded.bungeecord.listeners;


import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import me.fodded.core.managers.stats.loaders.RedisLoader;
import me.fodded.core.managers.stats.operators.RedisOperations;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.ForkJoinPool;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        // load statistics from the database and put it in redis
        ForkJoinPool.commonPool().submit(() -> {
            GeneralStats generalStats = new GeneralStats().getStatisticsFromDatabase(event.getPlayer().getUniqueId());
            RedisLoader.getInstance().uploadStatistics(event.getPlayer().getUniqueId(), generalStats);
            // We need this part to load ALL possible statistics (Sw, Bw, etc...)
            RedisOperations.getInstance().publishData("loadData", event.getPlayer().getUniqueId().toString());
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        // upload statistics to the database and remove from redis
        ForkJoinPool.commonPool().submit(() -> {
            GeneralStats generalStats = new GeneralStats().getStatisticsFromRedis(event.getPlayer().getUniqueId());
            DatabaseLoader.getInstance().uploadStatistics(event.getPlayer().getUniqueId(), generalStats);
            // We need this part to upload ALL possible statistics to the db (Sw, Bw, etc...)
            RedisOperations.getInstance().publishData("uploadData", event.getPlayer().getUniqueId().toString());

            // removing data from redis cache
            RedisOperations.getInstance().removeKeysContainingSubstring(event.getPlayer().getUniqueId().toString());
        });
    }
}
