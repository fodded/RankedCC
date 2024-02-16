package me.fodded.core.model;


import lombok.Getter;
import me.fodded.core.utils.mongodb.GsonSerializer;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class Redis {

    @Getter
    private RedissonClient redissonClient;

    public Redis(Config redisConfig) {
        redisConfig.setCodec(GsonSerializer.getInstance().createRedisCodec());
        redissonClient = Redisson.create(redisConfig);
    }
}
