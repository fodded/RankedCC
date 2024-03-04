package me.fodded.bungeecord.managers.friends;

import me.fodded.bungeecord.managers.FriendManager;
import me.fodded.bungeecord.utils.StringUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FriendAcceptRequest extends FriendManager implements IFriendRequest {

    private static FriendAcceptRequest instance;
    public FriendAcceptRequest() {
        instance = this;
    }

    @Override
    public void processRequest(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        if(!hasPlayerSentRequest(playerSentRequest, playerReceivedRequest)) {
            return;
        }

        addToFriends(playerSentRequest, playerReceivedRequest);
        addToFriends(playerReceivedRequest, playerSentRequest);

        String playerReceivedRequestPrefix = StringUtils.getPlayerPrefix(playerReceivedRequest);
        String acceptedRequest = StringUtils.getReplacedPlaceholders(
                StringUtils.getMessage(playerSentRequest, "friends.player-accepted-friend-req"),
                playerReceivedRequestPrefix
        );

        String playerSentRequestPrefix = StringUtils.getPlayerPrefix(playerSentRequest);
        String acceptedSentRequest = StringUtils.getReplacedPlaceholders(
                StringUtils.getMessage(playerReceivedRequest, "friends.player-accepted-friend-req"),
                playerSentRequestPrefix
        );

        playerSentRequest.sendMessage(StringUtils.format(acceptedRequest));
        playerReceivedRequest.sendMessage(StringUtils.format(acceptedSentRequest));
    }

    public static FriendAcceptRequest getInstance() {
        if(instance == null) {
            return new FriendAcceptRequest();
        }
        return instance;
    }
}
