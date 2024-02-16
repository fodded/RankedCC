package me.fodded.core.managers.stats.impl;

import com.mongodb.client.MongoCollection;
import me.fodded.core.model.GlobalDataManager;
import org.redisson.api.RMap;

import java.util.UUID;

public class GeneralStatsDataManager extends GlobalDataManager<UUID, GeneralStats> {

    public GeneralStatsDataManager(MongoCollection<GeneralStats> mongoCollection, RMap<UUID, GeneralStats> redissonMap) {
        super(mongoCollection, redissonMap, GeneralStats::new);
    }
}
