package me.fodded.spigotcore.gameplay.player;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.model.DataManager;
import me.fodded.core.model.GlobalDataManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.redisson.api.RTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractServerPlayer implements IServerPlayer {

    @Getter
    private final UUID uniqueId;
    private long lastTimeUsed = 0;

    private final static Map<UUID, AbstractServerPlayer> serverPlayerMap = new HashMap<>();

    public AbstractServerPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void loadDataAsync(Player player) {
        for (GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
            if (dataManager.isInRedis(player.getUniqueId())) {
                dataManager.getCachedValue(uniqueId);
                return;
            }
            dataManager.loadFromDatabaseToRedis(uniqueId);

            // we need to update the last name all the time player joins
            if (dataManager instanceof GeneralStatsDataManager) {
                GeneralStatsDataManager generalStatsDataManager = (GeneralStatsDataManager) dataManager;
                generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setLastName(player.getName()));
                generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setLastLogin(System.currentTimeMillis()));
                generalStatsDataManager.applyChangeToRedis(uniqueId, generalStats -> generalStats.setIpAddress(player.getAddress().getAddress().getHostAddress()));
            }

            dataManager.getCachedValue(uniqueId);
        }
    }

    public boolean isFlooding() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if(lastTimeUsed > System.currentTimeMillis()) {
            StringUtils.sendMessage(player, "chat-delay");
            return true;
        }

        lastTimeUsed = System.currentTimeMillis() + 3000;
        return false;
    }

    public static void sendLogToPlayers(String message) {
        CompletableFuture.runAsync(() -> {
            RTopic topic = Core.getInstance().getRedis().getRedissonClient().getTopic("sendLogsToPlayer");
            topic.publish(message);
        });
    }

    public void addPlayerToCache() {
        serverPlayerMap.put(uniqueId, this);
    }

    public void removePlayerFromList() {
        serverPlayerMap.remove(uniqueId);
    }

    public static AbstractServerPlayer getPlayerFromList(UUID uniqueId) {
        return serverPlayerMap.get(uniqueId);
    }

    public static boolean isInCache(UUID uniqueId) {
        return serverPlayerMap.containsKey(uniqueId);
    }
}

