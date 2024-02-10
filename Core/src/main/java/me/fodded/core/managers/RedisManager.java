package me.fodded.core.managers;

import me.fodded.core.Core;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private static RedisManager instance;

    public RedisManager() {
        instance = this;
    }

    public void pushData(String key, String value) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance().getPlugin(), () -> {
            Jedis jedis = null;
            try {
                jedis = Core.getInstance().getJedisPool().getResource();
                jedis.set(key, value);
            } finally {
                assert jedis != null;
                jedis.close();
            }
        });
    }

    public void initializeJedis() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.MAX_VALUE);

        Core.getInstance().setJedisPool(new JedisPool(jedisPoolConfig, "localhost", 6379));
    }

    public static RedisManager getInstance() {
        if(instance == null) {
            return new RedisManager();
        }

        return instance;
    }
}