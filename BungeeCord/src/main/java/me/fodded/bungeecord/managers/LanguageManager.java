package me.fodded.bungeecord.managers;

import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.proxycore.configs.ConfigLoader;
import net.md_5.bungee.config.Configuration;

import java.util.UUID;

public class LanguageManager {

    private static LanguageManager instance;
    public LanguageManager() {
        instance = this;
    }

    public Configuration getLanguageConfig(UUID uniqueId) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(uniqueId);
        return ConfigLoader.getInstance().getConfig(generalStats.getChosenLanguage() + "-lang.yml");
    }

    public static LanguageManager getInstance() {
        if(instance == null) {
            return new LanguageManager();
        }
        return instance;
    }
}
