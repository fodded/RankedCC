package me.fodded.core.managers.stats;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public abstract class Statistics {
    public abstract Statistics getStatisticsFromRedis(UUID uniqueId);
    public abstract Statistics getStatisticsFromDatabase(UUID uniqueId);
    public abstract Statistics getStatistics(UUID uniqueId, boolean isPlayerOnline);
}