package me.fodded.core.managers.stats.impl.skywars.ranked;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
public class RankedStats {

    private final UUID uuid;
    private long timePlayed;

    private int wins, kills, losses, deaths, selectedKitId;
    private List<RankedSeasonStats> rankedSeasonStatsList = new LinkedList<>();

    public RankedStats(UUID uuid) {
        this.uuid = uuid;
        this.selectedKitId = 1;
        rankedSeasonStatsList.add(new RankedSeasonStats(uuid));
    }

    public RankedSeasonStats getRankedSeasonStats(int season) {
        for(RankedSeasonStats rankedSeasonStats : rankedSeasonStatsList) {
            if(rankedSeasonStats.getSeason() == season) {
                return rankedSeasonStats;
            }
        }

        RankedSeasonStats rankedSeasonStats = new RankedSeasonStats(uuid);
        rankedSeasonStatsList.add(rankedSeasonStats);

        return rankedSeasonStats;
    }
}
