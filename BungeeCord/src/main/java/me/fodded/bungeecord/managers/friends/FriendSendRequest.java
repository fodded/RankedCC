package me.fodded.bungeecord.managers.friends;

import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FriendSendRequest extends FriendManager implements IFriendRequest {

    private static FriendSendRequest instance;
    public FriendSendRequest() {
        instance = this;
    }

    @Override
    public void processRequest(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        if(isPlayerFlooding(playerSentRequest) || hasPlayerSentRequest(playerSentRequest, playerReceivedRequest)) {
            StringUtils.sendMessage(playerSentRequest, "friends.frequent-friend-requests");
            return;
        }

        GeneralStats playerSentRequestGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(playerSentRequest.getUniqueId());
        if(playerSentRequestGeneralStats.getFriendList().size() >= 1000) {
            StringUtils.sendMessage(playerSentRequest, "friends.friend-limit");
            return;
        }

        if(playerSentRequestGeneralStats.getFriendList().contains(playerReceivedRequest.getUniqueId())) {
            StringUtils.sendMessage(playerSentRequest, "friends.has-player-friended");
            return;
        }

        GeneralStats playerReceivedRequestGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(playerReceivedRequest.getUniqueId());
        if(!playerReceivedRequestGeneralStats.isFriendRequestsEnabled() && !Rank.hasPermission(Rank.YOUTUBE, playerSentRequest.getUniqueId())) {
            StringUtils.sendMessage(playerSentRequest, "friends.cant-send-friend-request");
            return;
        }

        if(playerSentRequestGeneralStats.getFriendList().size() >= 1000) {
            StringUtils.sendMessage(playerSentRequest, "friends.cant-send-friend-request");
            return;
        }

        if(playerSentRequestGeneralStats.isVanished()) {
            StringUtils.sendMessage(playerSentRequest, "friends.no-player-present");
            return;
        }

        addPlayerSentRequestTo(playerSentRequest, playerReceivedRequest);
        FriendReceiveRequest.getInstance().processRequest(playerSentRequest, playerReceivedRequest);
    }

    public static FriendSendRequest getInstance() {
        if(instance == null) {
            return new FriendSendRequest();
        }
        return instance;
    }
}
