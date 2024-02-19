package me.fodded.core.managers.ranks;

import lombok.Getter;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;

import java.util.UUID;

@Getter
public enum Rank {

    ADMIN("&a&lADMIN &a", 7),
    MODERATOR("&2&lMODERATOR &2", 6),
    HELPER("&e&lHELPER &e", 5),
    MVPPLUS("&b&l&b ", 4),
    MVP("&b[MVP] ", 3),
    VIPPLUS("&a[VIP&6+&a] ", 2),
    VIP("&a[VIP] ", 1),
    DEFAULT("&7", 0);

    private final String prefix;
    private final int priority;

    Rank(String prefix, int priority) {
        this.prefix = prefix;
        this.priority = priority;
    }

    public static boolean hasPermission(Rank minimumRank, UUID uniqueId) {
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        GeneralStats generalStats = generalStatsDataManager.getCachedValue(uniqueId);

        int playerPriority = generalStats.getRank().getPriority();
        int requiredPriority = minimumRank.getPriority();

        return requiredPriority <= playerPriority;
    }
}
