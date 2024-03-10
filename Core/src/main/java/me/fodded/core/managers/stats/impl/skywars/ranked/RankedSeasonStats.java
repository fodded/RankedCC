package me.fodded.core.managers.stats.impl.skywars.ranked;

import lombok.Data;

import java.util.UUID;

@Data
public class RankedSeasonStats {

    private boolean banned;

    private final UUID uuid;
    private long timePlayed;

    private int rating, position, wins, kills, losses, deaths, season;
    private RankedDivision claimedDivision;
    private RankedStatus rankedStatus;

    public RankedSeasonStats(UUID uuid) {
        this.uuid = uuid;
        this.rankedStatus = RankedStatus.NONE;
        this.claimedDivision = RankedDivision.NONE;
        this.season = RankedStatsDataManager.getInstance().getCurrentSeason();
    }
}
