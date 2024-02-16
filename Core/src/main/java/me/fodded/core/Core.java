package me.fodded.core;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.impl.GeneralStatsDataManager;
import me.fodded.core.model.Database;
import me.fodded.core.model.Redis;
import org.redisson.config.Config;

@Getter
public class Core {

    @Getter
    private static Core instance;

    private final Redis redis;
    private final Database database;

    private final GeneralStatsDataManager generalStatsDataManager;

    private Core(String mongodbConnection, Config redisConfig) {
        instance = this;

        redis = new Redis(redisConfig);
        database = new Database(mongodbConnection);

        generalStatsDataManager = new GeneralStatsDataManager(
                database.getMongoDatabase(Database.STATISTICS_DB).getCollection("generalStats", GeneralStats.class),
                redis.getRedissonClient().getMap("generalStats")
        );
    }

    public static void initialize(String mongodbConnection, Config redisConfig) {
        Preconditions.checkState(instance == null, "The core is already initialized");
        new Core(
                mongodbConnection,
                redisConfig
        );
    }
}