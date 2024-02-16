package me.fodded.skywars.managers.stats;

import com.mongodb.client.MongoCollection;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.model.GlobalDataManager;
import org.redisson.api.RMap;

import java.util.UUID;

public class RankedSkywarsStatsDataManager extends GlobalDataManager<UUID, RankedSkywarsStats> {

    public RankedSkywarsStatsDataManager(MongoCollection<RankedSkywarsStats> mongoCollection, RMap<UUID, RankedSkywarsStats> redissonMap) {
        super(mongoCollection, redissonMap, RankedSkywarsStats::new);
    }
}
