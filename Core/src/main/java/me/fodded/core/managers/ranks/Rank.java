package me.fodded.core.managers.ranks;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.impl.GeneralStatsDataManager;

import java.util.UUID;

@Getter
public enum Rank {

    ADMIN("&c[ADMIN] ", 7),
    MODERATOR("&2[MODERATOR] ", 6),
    HELPER("&9[HELPER] ", 5),
    MVPPLUS("&b[MVP&c+&b] ", 4),
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
        GeneralStatsDataManager generalStatsDataManager = Core.getInstance().getGeneralStatsDataManager();
        GeneralStats generalStats = generalStatsDataManager.getCachedValue(uniqueId);

        int playerPriority = generalStats.getRank().getPriority();
        int requiredPriority = minimumRank.getPriority();

        return requiredPriority <= playerPriority;
    }
}
