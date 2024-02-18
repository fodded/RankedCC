package me.fodded.core;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.fodded.core.model.Database;
import me.fodded.core.model.Redis;
import org.redisson.config.Config;

@Getter
public class Core {

    @Getter
    private static Core instance;

    private Redis redis;
    private Database database;

    private Core() {
        instance = this;
    }

    public void initializeRedis(Config redisConfig) {
        redis = new Redis(redisConfig);
    }

    public void initializeDatabase(String mongodbConnection) {
        database = new Database(mongodbConnection);
    }

    public static void initialize() {
        Preconditions.checkState(instance == null, "The core is already initialized");
        new Core();
    }
}