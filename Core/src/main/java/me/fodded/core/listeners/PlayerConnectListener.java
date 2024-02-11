package me.fodded.core.listeners;

import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.loaders.StatisticsDatabaseLoader;
import me.fodded.core.managers.stats.loaders.StatisticsRedisLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // we pass general statistics to redis so bungee can deal with it later
        GeneralStats generalStats = new GeneralStats().getStatistics(player.getUniqueId());
        new StatisticsRedisLoader().uploadStatistics(player.getUniqueId(), generalStats);

        StatisticsDatabaseLoader.getInstance().uploadStatistics(player.getUniqueId(), generalStats);
        generalStats.removeStatistics();
    }
}
