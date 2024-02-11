package me.fodded.core.managers.stats.loaders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import me.fodded.core.Core;
import me.fodded.core.managers.stats.Statistics;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class StatisticsRedisLoader implements IStatisticsLoader {

    private static StatisticsRedisLoader instance;
    public StatisticsRedisLoader() {
        instance = this;
    }

    @Override
    public void uploadStatistics(UUID uniqueId, Statistics statistics) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance().getPlugin(), () -> {
            Jedis jedis = null;
            try {
                Gson gson = new GsonBuilder()
                        .create();
                String json = gson.toJson(statistics);

                jedis = Core.getInstance().getJedisPool().getResource();
                jedis.set(statistics.getClass().getSimpleName() + ":" + uniqueId.toString(), json);
            } finally {
                assert jedis != null;
                jedis.close();
            }
        });
    }

    /*
     * If there is no data representation in redis
     * It means it couldn't load anything from the database
     * Therefore we create a completely new player data
     * And then when a player leaves we pass data to redis
     * Then bungeecord side takes the data from redis and puts in the db
     */
    @SneakyThrows
    @Override
    public Statistics loadStatistics(UUID uniqueId, Class statisticsClass) {
        Jedis jedis = null;
        try {
            Gson gson = new GsonBuilder()
                    .create();

            jedis = Core.getInstance().getJedisPool().getResource();
            if(!jedis.exists(statisticsClass.getSimpleName() + ":" + uniqueId.toString())) {
                return null;
            }

            String serializedPlayerData = jedis.get(statisticsClass.getSimpleName() + ":" + uniqueId);
            return (Statistics) gson.fromJson(serializedPlayerData, statisticsClass);
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public static StatisticsRedisLoader getInstance() {
        if(instance == null) {
            return new StatisticsRedisLoader();
        }

        return instance;
    }
}
