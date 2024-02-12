package me.fodded.core.managers.stats.impl;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.managers.stats.Statistics;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import me.fodded.core.managers.stats.loaders.RedisLoader;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@Getter @Setter
public class GeneralStats extends Statistics {

    @SerializedName("uniqueId")
    private UUID uniqueId;

    @SerializedName("rank")
    private RankType rank;

    private String prefix, displayedName, name, chosenLanguage;
    private boolean vanished, logging, playersVisibility, chatEnabled;

    public GeneralStats() {

    }

    public GeneralStats(UUID uniqueId) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uniqueId);

        this.uniqueId = uniqueId;
        this.rank = RankType.DEFAULT;

        this.prefix = "";
        this.name = player.getName();
        this.displayedName = player.getName();
        this.chosenLanguage = "ru";

        this.chatEnabled = true;
        this.playersVisibility = true;
    }

    @Override
    public GeneralStats getStatisticsFromRedis(UUID uniqueId) {
        GeneralStats generalStats = (GeneralStats) RedisLoader.getInstance().loadStatistics(uniqueId, GeneralStats.class);
        if(generalStats != null) {
            return generalStats;
        }
        return new GeneralStats(uniqueId);
    }

    @Override
    public GeneralStats getStatisticsFromDatabase(UUID uniqueId) {
        GeneralStats generalStats = (GeneralStats) DatabaseLoader.getInstance().loadStatistics(uniqueId, GeneralStats.class);
        if(generalStats != null) {
            return generalStats;
        }

        return new GeneralStats(uniqueId);
    }

    @Override
    public GeneralStats getStatistics(UUID uniqueId, boolean isPlayerOnline) {
        if(isPlayerOnline) {
            return getStatisticsFromRedis(uniqueId);
        }
        return getStatisticsFromDatabase(uniqueId);
    }
}
