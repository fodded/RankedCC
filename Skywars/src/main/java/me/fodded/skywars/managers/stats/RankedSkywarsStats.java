package me.fodded.skywars.managers.stats;

import lombok.Data;

import java.util.UUID;

@Data
public class RankedSkywarsStats {

    private UUID uniqueId;

    private int wins, kills, losses, deaths;
    private long experience, timePlayed;

    public RankedSkywarsStats(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
