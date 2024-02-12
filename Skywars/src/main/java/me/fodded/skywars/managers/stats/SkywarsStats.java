package me.fodded.skywars.managers.stats;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import me.fodded.core.managers.stats.Statistics;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import me.fodded.core.managers.stats.loaders.RedisLoader;

import java.util.HashMap;
import java.util.UUID;

@Getter @Setter
public class SkywarsStats extends Statistics {

    @SerializedName("uniqueId")
    private UUID uniqueId;

    private int wins, kills, losses, deaths;
    private long experience, timePlayed;

    public SkywarsStats() {

    }

    public SkywarsStats(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public SkywarsStats getStatistics(UUID uniqueId, boolean loadFromDatabase) {
        if(loadFromDatabase) {
            return (SkywarsStats) DatabaseLoader.getInstance().loadStatistics(uniqueId, SkywarsStats.class);
        }

        SkywarsStats skywarsStats = (SkywarsStats) RedisLoader.getInstance().loadStatistics(uniqueId, SkywarsStats.class);
        if(skywarsStats != null) {
            return skywarsStats;
        }

        return new SkywarsStats(uniqueId);
    }
}
