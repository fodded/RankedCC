package me.fodded.skywars.listeners.jedis;

import me.fodded.core.managers.stats.Statistics;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import me.fodded.core.managers.stats.loaders.RedisLoader;
import me.fodded.skywars.managers.stats.SkywarsStats;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

public class JedisListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equalsIgnoreCase("loadData")) {
            UUID uniqueId = UUID.fromString(message);

            ForkJoinPool.commonPool().submit(() -> {
                SkywarsStats skywarsStats = new SkywarsStats().getStatistics(uniqueId, true);
                RedisLoader.getInstance().uploadStatistics(uniqueId, skywarsStats);
            });
        }

        if(channel.equalsIgnoreCase("uploadData")) {
            UUID uniqueId = UUID.fromString(message);

            ForkJoinPool.commonPool().submit(() -> {
                Statistics skywarsStats = new SkywarsStats().getStatistics(uniqueId, false);
                DatabaseLoader.getInstance().uploadStatistics(uniqueId, skywarsStats);
            });
        }
    }
}