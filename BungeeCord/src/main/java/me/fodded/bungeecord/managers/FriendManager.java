package me.fodded.bungeecord.managers;

import lombok.Data;
import me.fodded.bungeecord.Main;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Data
public class FriendManager {

    private static FriendManager instance;
    private static final Map<UUID, Long> floodMap = new HashMap<>();
    private static final Map<UUID, Integer> sentRequestsCounterMap = new HashMap<>();
    private static final Map<UUID, List<UUID>> sentRequestsMap = new HashMap<>();

    public FriendManager() {
        instance = this;
    }

    //TODO: make async
    public void addToFriends(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        generalStatsDataManager.applyChange(
                playerSentRequest.getUniqueId(),
                generalStats -> generalStats.addFriendToFriendList(playerReceivedRequest.getUniqueId())
        );

        removePlayerSentRequestTo(playerSentRequest, playerReceivedRequest);
    }

    public void removeFromFriends(UUID playerSentRequestUniqueId, UUID playerReceivedRequestUniqueId) {
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        generalStatsDataManager.applyChange(
                playerSentRequestUniqueId,
                generalStats -> generalStats.removeFriendFromFriendList(playerReceivedRequestUniqueId)
        );
    }

    public void addPlayerSentRequestTo(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        List<UUID> sentPlayersRequestsList = getSentRequestsList(playerSentRequest);
        sentPlayersRequestsList.add(playerReceivedRequest.getUniqueId());

        sentRequestsMap.put(playerSentRequest.getUniqueId(), sentPlayersRequestsList);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> {
            removePlayerSentRequestTo(playerSentRequest, playerReceivedRequest);
        }, 3, TimeUnit.MINUTES);
    }

    public void removePlayerSentRequestTo(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        List<UUID> sentPlayersRequestsList = getSentRequestsList(playerSentRequest);
        sentPlayersRequestsList.remove(playerReceivedRequest.getUniqueId());

        sentRequestsMap.put(playerSentRequest.getUniqueId(), sentPlayersRequestsList);
    }

    public boolean hasPlayerSentRequest(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        return getSentRequestsList(playerSentRequest).contains(playerReceivedRequest.getUniqueId());
    }

    private List<UUID> getSentRequestsList(ProxiedPlayer playerSentRequest) {
        if(sentRequestsMap.containsKey(playerSentRequest.getUniqueId())) {
            return sentRequestsMap.get(playerSentRequest.getUniqueId());
        }

        return new ArrayList<>();
    }

    protected boolean isPlayerFlooding(ProxiedPlayer player) {
        increaseSentRequests(player);
        if(floodMap.containsKey(player.getUniqueId())) {
            return floodMap.get(player.getUniqueId()) > System.currentTimeMillis();
        }

        if(sentRequestsCounterMap.get(player.getUniqueId()) > 10) {
            floodMap.put(player.getUniqueId(), System.currentTimeMillis() + 1000 * 60);
            sentRequestsCounterMap.remove(player.getUniqueId());
        }

        return false;
    }

    private void increaseSentRequests(ProxiedPlayer player) {
        sentRequestsCounterMap.put(player.getUniqueId(), sentRequestsCounterMap.getOrDefault(player.getUniqueId(), 0)+1);
    }

    public static FriendManager getInstance() {
        if(instance == null) {
            return new FriendManager();
        }
        return instance;
    }
}
