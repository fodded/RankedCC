package me.fodded.bungeecord.managers;

import lombok.Data;
import me.fodded.bungeecord.Main;
import me.fodded.bungeecord.managers.friends.FriendAcceptRequest;
import me.fodded.bungeecord.managers.friends.FriendDeclineRequest;
import me.fodded.bungeecord.managers.friends.FriendSendRequest;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
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
    public void addFriend(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        generalStatsDataManager.applyChangeToRedis(
                playerSentRequest.getUniqueId(),
                generalStats -> generalStats.addFriendToFriendList(playerReceivedRequest.getUniqueId())
        );

        removePlayerSentRequestTo(playerSentRequest, playerReceivedRequest);
    }

    public void removeFriend(UUID playerSentRequestUniqueId, UUID playerReceivedRequestUniqueId) {
        GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
        generalStatsDataManager.applyChangeToRedis(
                playerSentRequestUniqueId,
                generalStats -> generalStats.removeFriendFromFriendList(playerReceivedRequestUniqueId)
        );
    }

    public void proceedFriendRemove(ProxiedPlayer playerSentRequest, String playerReceivedRequestName) {
        UUID playerReceivedRequestUUID = DatabaseOperations.getInstance().getUniqueIdFromName(playerReceivedRequestName);
        if(playerReceivedRequestUUID == null) {
            StringUtils.sendMessage(playerSentRequest, "friends.no-such-player");
            return;
        }

        GeneralStats playerSentRequestStats = GeneralStatsDataManager.getInstance().getCachedValue(playerSentRequest.getUniqueId());
        if(!playerSentRequestStats.getFriendList().contains(playerReceivedRequestUUID)) {
            StringUtils.sendMessage(playerSentRequest, "friends.not-friends");
            return;
        }

        removeFriend(playerSentRequest.getUniqueId(), playerReceivedRequestUUID);
        removeFriend(playerReceivedRequestUUID, playerSentRequest.getUniqueId());

        playerSentRequest.sendMessage(StringUtils.getReplacedPlaceholders(
                StringUtils.getMessage(playerSentRequest, "friends.player-removed-friend"),
                StringUtils.getPlayerPrefix(playerReceivedRequestUUID)
        ));
    }

    public void proceedFriendRequest(ProxiedPlayer playerSentRequest, String playerReceivedRequestName) {
        ProxiedPlayer playerReceivedRequest = ProxyServer.getInstance().getPlayer(playerReceivedRequestName);
        if(playerReceivedRequest == null) {
            StringUtils.sendMessage(playerSentRequest, "friends.no-player-present");
            return;
        }

        FriendSendRequest.getInstance().processRequest(playerSentRequest, playerReceivedRequest);
    }

    public void acceptFriendRequest(ProxiedPlayer playerReceivedRequest, String playerSentRequestName) {
        ProxiedPlayer playerSentRequest = ProxyServer.getInstance().getPlayer(playerSentRequestName);
        if(playerSentRequest == null) {
            StringUtils.sendMessage(playerReceivedRequest, "friends.no-player-present");
            return;
        }

        GeneralStats playerGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(playerReceivedRequest.getUniqueId());
        if(playerGeneralStats.getFriendList().size() >= 1000) {
            StringUtils.sendMessage(playerReceivedRequest, "friends.friend-limit");
            return;
        }

        FriendAcceptRequest.getInstance().processRequest(playerSentRequest, playerReceivedRequest);
    }

    public void declineFriendRequest(ProxiedPlayer playerReceivedRequest, String playerSentRequestName) {
        ProxiedPlayer playerSentRequest = ProxyServer.getInstance().getPlayer(playerSentRequestName);
        if(playerSentRequest == null) {
            StringUtils.sendMessage(playerReceivedRequest, "friends.no-player-present");
            return;
        }

        FriendDeclineRequest.getInstance().processRequest(playerSentRequest, playerReceivedRequest);
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
