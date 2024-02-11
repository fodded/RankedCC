package me.fodded.core.managers.stats.impl;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.managers.stats.Statistics;
import me.fodded.core.managers.stats.loaders.StatisticsDatabaseLoader;
import me.fodded.core.managers.stats.loaders.StatisticsRedisLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter @Setter
public class GeneralStats extends Statistics {

    private static HashMap<UUID, GeneralStats> generalStatsMap = new HashMap<>();

    @SerializedName("uniqueId")
    private UUID uniqueId;

    @SerializedName("rank")
    private RankType rank;

    private String prefix, displayedName, name;
    private boolean vanished, logging, playersVisibility, chatEnabled;

    public GeneralStats() {

    }

    public GeneralStats(UUID uniqueId) {
        Player player = Bukkit.getPlayer(uniqueId);

        this.uniqueId = uniqueId;
        this.rank = RankType.DEFAULT;

        this.prefix = "";
        this.name = player.getName();
        this.displayedName = player.getName();

        this.chatEnabled = true;
        this.playersVisibility = true;

        generalStatsMap.put(uniqueId, this);
    }

    @Override
    public GeneralStats getStatistics(UUID uniqueId) {
        if(generalStatsMap.containsKey(uniqueId)) {
            return generalStatsMap.get(uniqueId);
        }

        GeneralStats generalStats = (GeneralStats) StatisticsRedisLoader.getInstance().loadStatistics(uniqueId, GeneralStats.class);
        if(generalStats != null) {
            generalStatsMap.put(uniqueId, generalStats);
            return generalStats;
        }

        return new GeneralStats(uniqueId);
    }

    public void removeStatistics() {
        generalStatsMap.remove(uniqueId);
    }
}
