package me.fodded.bungeecord.managers.friends;

import me.fodded.bungeecord.utils.StringUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FriendDeclineRequest extends FriendManager implements IFriendRequest {

    private static FriendDeclineRequest instance;
    public FriendDeclineRequest() {
        instance = this;
    }

    @Override
    public void processRequest(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        if(!hasPlayerSentRequest(playerSentRequest, playerReceivedRequest)) {
            return;
        }

        removePlayerSentRequestTo(playerSentRequest, playerReceivedRequest);
        String declineMessage = StringUtils.getReplacedPlaceholders(
                StringUtils.getMessage(playerReceivedRequest, "friends.player-declined-friend-req"),
                StringUtils.getPlayerPrefix(playerSentRequest)
        );

        playerReceivedRequest.sendMessage(StringUtils.format(declineMessage));
    }

    public static FriendDeclineRequest getInstance() {
        if(instance == null) {
            return new FriendDeclineRequest();
        }
        return instance;
    }
}
