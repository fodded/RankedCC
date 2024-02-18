package me.fodded.core.model;

import lombok.Getter;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.impl.skywars.ranked.RankedStatsDataManager;

import java.util.LinkedList;
import java.util.List;

public class DataManager {

    private static DataManager instance;
    @Getter
    private final List<GlobalDataManager> statisticsList = new LinkedList<>();

    private DataManager() {
        instance = this;

        // Profile
        statisticsList.add(GeneralStatsDataManager.getInstance());

        // Skywars
        statisticsList.add(RankedStatsDataManager.getInstance());
    }

    public static DataManager getInstance() {
        if(instance == null) {
            return new DataManager();
        }
        return instance;
    }
}
