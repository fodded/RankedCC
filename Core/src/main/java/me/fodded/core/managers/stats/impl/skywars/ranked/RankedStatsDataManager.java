package me.fodded.core.managers.stats.impl.skywars.ranked;

import com.mongodb.client.MongoCollection;
import me.fodded.core.Core;
import me.fodded.core.model.Database;
import me.fodded.core.model.GlobalDataManager;
import org.redisson.api.RMap;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class RankedStatsDataManager extends GlobalDataManager<UUID, RankedStats> {

    private static RankedStatsDataManager instance;
    public RankedStatsDataManager(MongoCollection<RankedStats> mongoCollection, RMap<UUID, RankedStats> redissonMap) {
        super(mongoCollection, redissonMap, RankedStats::new);
        instance = this;
    }

    public int getCurrentSeason() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());

        long monthsBetween = ChronoUnit.MONTHS.between(
                LocalDate.parse("2016-04-13").withDayOfMonth(1),
                LocalDate.parse(date).withDayOfMonth(1));

        return (int) (monthsBetween + 1) - 16;
    }

    public static RankedStatsDataManager getInstance() {
        if(instance == null) {
            return new RankedStatsDataManager(
                    Core.getInstance().getDatabase().getMongoDatabase(Database.STATISTICS_DB).getCollection("RankedStats", RankedStats.class),
                    Core.getInstance().getRedis().getRedissonClient().getMap("rankedStats")
            );
        }
        return instance;
    }
}
