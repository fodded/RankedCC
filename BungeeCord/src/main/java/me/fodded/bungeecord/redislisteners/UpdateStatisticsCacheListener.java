package me.fodded.bungeecord.redislisteners;

import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UpdateStatisticsCacheListener implements IBungeeRedisListener {

    @Override
    public void onMessage(CharSequence channel, Object msg) {
        CompletableFuture.runAsync(() -> {
            UUID playerUniqueId = UUID.fromString(msg.toString());
            GeneralStatsDataManager.getInstance().updateCache(playerUniqueId);
        });
    }
}
