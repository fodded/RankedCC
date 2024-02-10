package me.fodded.skywars.managers.stats;

import lombok.Getter;
import lombok.Setter;
import me.fodded.core.managers.stats.Statistics;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.loaders.StatisticsRedisLoader;

import java.util.HashMap;
import java.util.UUID;

@Getter @Setter
public class SkywarsStats extends Statistics {

    private static HashMap<UUID, SkywarsStats> skywarsStatsMap = new HashMap<>();

    private UUID uniqueId;

    private int wins, kills, losses, deaths;
    private long experience, timePlayed;

    public SkywarsStats() {

    }

    public SkywarsStats(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public SkywarsStats getStatistics(UUID uniqueId) {
        if(skywarsStatsMap.containsKey(uniqueId)) {
            return skywarsStatsMap.get(uniqueId);
        }

        SkywarsStats skywarsStats = (SkywarsStats) new StatisticsRedisLoader().loadStatistics(uniqueId, SkywarsStats.class);
        if(skywarsStats == null) {
            skywarsStats = new SkywarsStats(uniqueId);
        }

        return skywarsStats;
    }
}
