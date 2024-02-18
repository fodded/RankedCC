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

    public RankedSeasonStats(UUID uuid) {
        this.uuid = uuid;
        this.claimedDivision = RankedDivision.NONE;
        this.season = RankedStatsDataManager.getInstance().getCurrentSeason();
    }
}
