package me.fodded.skywars.listeners.jedis;

import me.fodded.core.Core;
import me.fodded.core.managers.stats.Statistics;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import me.fodded.core.managers.stats.loaders.RedisLoader;
import me.fodded.skywars.managers.stats.SkywarsStats;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

public class JedisListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if(channel.equalsIgnoreCase("loadData")) {
            UUID uniqueId = UUID.fromString(message);

            ForkJoinPool.commonPool().submit(() -> {
                SkywarsStats skywarsStats = new SkywarsStats().getStatisticsFromDatabase(uniqueId);
                RedisLoader.getInstance().uploadStatistics(uniqueId, skywarsStats);
            });
        }

        if(channel.equalsIgnoreCase("uploadData")) {
            UUID uniqueId = UUID.fromString(message);

            ForkJoinPool.commonPool().submit(() -> {
                Statistics skywarsStats = new SkywarsStats().getStatisticsFromRedis(uniqueId);
                DatabaseLoader.getInstance().uploadStatistics(uniqueId, skywarsStats);
            });
        }

        if(channel.equalsIgnoreCase("editData")) {
            ForkJoinPool.commonPool().submit(() -> {
                editRedisStatistics(message);
            });
        }
    }

    private void editRedisStatistics(String message) {
        UUID uniqueId = UUID.fromString(message.split(":")[1]);

        String statisticName = message.split(":")[0];
        statisticName = statisticName.substring(0, statisticName.length()-5);

        String serverName = Core.getInstance().getServerName();
        if(!serverName.contains(statisticName)) {
            return;
        }

        String fieldName = message.split(":")[2];
        String passedValue = message.split(":")[3];

        SkywarsStats skywarsStats = getUpdatedStatistics(uniqueId, fieldName, passedValue);
        RedisLoader.getInstance().uploadStatistics(uniqueId, skywarsStats);
    }

    private SkywarsStats getUpdatedStatistics(UUID uniqueId, String fieldName, String passedValue) {
        SkywarsStats skywarsStats = new SkywarsStats().getStatisticsFromRedis(uniqueId);
        for (Field field : skywarsStats.getClass().getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                try {
                    Object newValue = getNewValue(passedValue, field);

                    field.setAccessible(true);
                    field.set(skywarsStats, newValue);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return skywarsStats;
    }

    private Object getNewValue(String passedValue, Field field) {
        Object newValue = passedValue;
        final Class<?> type = field.getType();
        if (int.class.isAssignableFrom(type)) {
            newValue = Integer.parseInt(passedValue);
        } else if (long.class.isAssignableFrom(type)) {
            newValue = Long.parseLong(passedValue);
        }
        return newValue;
    }
}