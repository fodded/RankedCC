package me.fodded.core.managers.stats;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

    @Getter
    private JedisPool jedisPool;
    @Getter
    private static Redis instance;

    public Redis(String host, int port) {
        instance = this;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(Integer.MAX_VALUE);

        jedisPool = new JedisPool(jedisPoolConfig, host, port);
    }

}
