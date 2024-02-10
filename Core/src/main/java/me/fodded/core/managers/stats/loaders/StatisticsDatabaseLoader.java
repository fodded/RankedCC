package me.fodded.core.managers.stats.loaders;

import me.fodded.core.managers.stats.Statistics;

import java.util.UUID;

public class StatisticsDatabaseLoader implements IStatisticsLoader {

    @Override
    public void uploadStatistics(UUID uniqueId, Statistics statistics) {

    }

    @Override
    public Statistics loadStatistics(UUID uniqueId, Class statisticsClass) {
        return null;
    }
}
