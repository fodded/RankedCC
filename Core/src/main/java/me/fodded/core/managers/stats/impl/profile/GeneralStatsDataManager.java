package me.fodded.core.managers.stats.impl.profile;

import com.mongodb.client.MongoCollection;
import me.fodded.core.Core;
import me.fodded.core.model.Database;
import me.fodded.core.model.GlobalDataManager;
import org.redisson.api.RMap;

import java.util.UUID;

public class GeneralStatsDataManager extends GlobalDataManager<UUID, GeneralStats> {

    private static GeneralStatsDataManager instance;

    public GeneralStatsDataManager(MongoCollection<GeneralStats> mongoCollection, RMap<UUID, GeneralStats> redissonMap) {
        super(mongoCollection, redissonMap, GeneralStats::new);
        instance = this;
    }

    public static GeneralStatsDataManager getInstance() {
        if(instance == null) {
            return new GeneralStatsDataManager(
                    Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB).getCollection("GeneralStats", GeneralStats.class),
                    Core.getInstance().getRedis().getRedissonClient().getMap("generalStats")
            );
        }
        return instance;
    }
}
