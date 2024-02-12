package me.fodded.core.managers.stats.operators;

import me.fodded.core.managers.stats.Redis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisOperations {
    private static RedisOperations instance;

    public RedisOperations() {
        instance = this;
    }

    public boolean isKeyPresent(String key) {
        Jedis jedis = null;
        try {
            jedis = Redis.getInstance().getJedisPool().getResource();
            return jedis.exists(key);
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public void insertData(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = Redis.getInstance().getJedisPool().getResource();
            jedis.set(key, value);
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public void publishData(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = Redis.getInstance().getJedisPool().getResource();
            jedis.publish(channel, message);
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public void removeData(String data) {
        Jedis jedis = null;
        try {
            jedis = Redis.getInstance().getJedisPool().getResource();
            jedis.del(data);
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public void removeKeysContainingSubstring(String substring) {
        ScanParams scanParams = new ScanParams().match("*" + substring + "*");
        String cursor = "0";

        do {
            ScanResult<String> scanResult = Redis.getInstance().getJedisPool().getResource().scan(cursor, scanParams);
            for (String key : scanResult.getResult()) {
                Redis.getInstance().getJedisPool().getResource().del(key);
            }
            cursor = scanResult.getStringCursor();
        } while (!"0".equals(cursor));
    }

    public static RedisOperations getInstance() {
        if(instance == null) {
            return new RedisOperations();
        }

        return instance;
    }
}
