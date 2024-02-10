package me.fodded.core.managers.ranks;

import me.fodded.core.managers.stats.impl.GeneralStats;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class RankManager {

    private static RankManager instance;
    private final List<Rank> ranksList = new ArrayList<>();

    public RankManager() {
        instance = this;
    }

    public void initializeRanks() {
        ranksList.add(new Rank(RankType.DEFAULT, "&7", 0));
        ranksList.add(new Rank(RankType.VIP, "&aVIP ", 1));
        ranksList.add(new Rank(RankType.VIPPLUS, "&aVIP&6+ &a", 2));
        ranksList.add(new Rank(RankType.MVP, "&bMVP ", 3));
        ranksList.add(new Rank(RankType.MVPPLUS, "&bMVP&c+ &b", 4));
        ranksList.add(new Rank(RankType.HELPER, "&9HELPER ", 5));
        ranksList.add(new Rank(RankType.MODERATOR, "&2MODERATOR ", 6));
        ranksList.add(new Rank(RankType.ADMIN, "&cADMIN ", 7));
    }

    private int getPriority(RankType rankType) {
        for(Rank rank : ranksList) {
            if(rank.getRank().equals(rankType)) {
                return rank.getPriority();
            }
        }

        return 0;
    }

    public boolean canRunCommand(RankType rank, UUID uniqueId) {
        if(Bukkit.getPlayer(uniqueId).isOp()) {
            return true;
        }

        GeneralStats statistics = new GeneralStats(uniqueId).getStatistics(uniqueId);

        int playerPriority = getPriority(statistics.getRank());
        int requiredPriority = getPriority(rank);

        return requiredPriority <= playerPriority;
    }

    public Rank getRank(RankType rankType) {
        for(Rank rank : ranksList) {
            if(rank.getRank().equals(rankType)) {
                return rank;
            }
        }
        return null;
    }

    public static RankManager getInstance() {
        if(instance == null) {
            return new RankManager();
        }

        return instance;
    }
}
