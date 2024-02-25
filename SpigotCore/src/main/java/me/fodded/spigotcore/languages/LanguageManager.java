package me.fodded.spigotcore.languages;

import lombok.Getter;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.configs.ConfigLoader;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LanguageManager {

    private static LanguageManager instance;

    @Getter
    private final List<String> langaugesList;

    public LanguageManager() {
        instance = this;
        langaugesList = new LinkedList<>();
    }

    public void initializeLanguageList() {
        JavaPlugin plugin = SpigotCore.getInstance().getPlugin();
        File[] files = plugin.getDataFolder().listFiles();

        langaugesList.clear();
        for(File file : files) {
            if (!file.getName().contains("-lang.yml")) {
                continue;
            }
            String shortenedName = file.getName().split("-")[0];
            langaugesList.add(shortenedName);
        }
    }

    public Configuration getLanguageConfig(UUID uniqueId) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(uniqueId);
        return ConfigLoader.getInstance().getConfig(generalStats.getChosenLanguage() + "-lang.yml");
    }

    private int getLanguageIndex(String language) {
        List<String> languagesList = langaugesList;
        for(int i = 0; i < languagesList.size(); i++) {
            if(languagesList.get(i).equalsIgnoreCase(language)) {
                return i;
            }
        }
        return 0;
    }

    public String getNextLanguage(String currentLanguage) {
        List<String> languagesList = langaugesList;
        int currentLanguageIndex = getLanguageIndex(currentLanguage);

        if(languagesList.size()-1<currentLanguageIndex+1) {
            return languagesList.get(0);
        }
        return languagesList.get(currentLanguageIndex+1);
    }

    public String getPreviousLanguage(String currentLanguage) {
        List<String> languagesList = langaugesList;
        int currentLanguageIndex = getLanguageIndex(currentLanguage);

        if(currentLanguageIndex-1 < 0) {
            return languagesList.get(languagesList.size()-1);
        }
        return languagesList.get(currentLanguageIndex-1);
    }

    public static LanguageManager getInstance() {
        if(instance == null) {
            return new LanguageManager();
        }
        return instance;
    }
}
