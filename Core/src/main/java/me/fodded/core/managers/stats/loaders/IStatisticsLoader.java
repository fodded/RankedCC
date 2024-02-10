package me.fodded.core.managers.stats.loaders;

import lombok.SneakyThrows;
import me.fodded.core.managers.stats.Statistics;

import java.util.UUID;

public interface IStatisticsLoader {
    void uploadStatistics(UUID uniqueId, Statistics statistics);

    @SneakyThrows
    Statistics loadStatistics(UUID uniqueId, Class statisticsClass);
}
