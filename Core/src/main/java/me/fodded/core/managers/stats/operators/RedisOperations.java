package me.fodded.core.managers.stats.operators;

import lombok.Getter;
import lombok.Setter;
import me.fodded.core.managers.stats.Redis;
import redis.clients.jedis.*;

import java.util.Set;

public class RedisOperations {
    private static RedisOperations instance;

    public RedisOperations() {
        instance = this;
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
