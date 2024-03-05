package me.fodded.bungeecord.managers.friends;

import me.fodded.bungeecord.utils.StringUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FriendReceiveRequest extends FriendManager implements IFriendRequest {

    private static FriendReceiveRequest instance;
    public FriendReceiveRequest() {
        instance = this;
    }

    @Override
    public void processRequest(ProxiedPlayer playerSentRequest, ProxiedPlayer playerReceivedRequest) {
        String playerPrefix = StringUtils.getPlayerPrefix(playerSentRequest);
        TextComponent receivedTextComponent = StringUtils.getTextComponent(
                StringUtils.getReplacedPlaceholders(StringUtils.getMessage(
                        playerReceivedRequest, "friends.request-message.received"),
                        playerPrefix
                ),
                null,
                null
        );

        TextComponent pickActionComponent = StringUtils.getTextComponent(
                StringUtils.getMessage(playerReceivedRequest, "friends.request-message.received-button"),
                null,
                null
        );

        TextComponent acceptTextComponent = StringUtils.getTextComponent(
                StringUtils.getReplacedPlaceholders(StringUtils.getMessage(playerReceivedRequest, "friends.request-message.accept.prefix"), playerPrefix) + " ",
                StringUtils.getReplacedPlaceholders(StringUtils.getMessage(playerReceivedRequest, "friends.request-message.accept.message"), playerPrefix) + " ",
                "/friend accept " + playerSentRequest.getName()
        );

        TextComponent declineTextComponent = StringUtils.getTextComponent(
                StringUtils.getReplacedPlaceholders(StringUtils.getMessage(playerReceivedRequest,"friends.request-message.decline.prefix"), playerPrefix),
                StringUtils.getReplacedPlaceholders(StringUtils.getMessage(playerReceivedRequest, "friends.request-message.decline.message"), playerPrefix),
                "/friend decline " + playerSentRequest.getName()
        );

        playerReceivedRequest.sendMessage(StringUtils.format("&f--------------------------------------"));
        playerReceivedRequest.sendMessage(receivedTextComponent);
        playerReceivedRequest.sendMessage(pickActionComponent, acceptTextComponent, declineTextComponent);
        playerReceivedRequest.sendMessage(StringUtils.format("&f--------------------------------------"));

        playerSentRequest.sendMessage(StringUtils.getReplacedPlaceholders(
                StringUtils.getMessage(playerSentRequest, "friends.sent-friend-request"),
                StringUtils.getPlayerPrefix(playerReceivedRequest)
        ));
    }

    public static FriendReceiveRequest getInstance() {
        if(instance == null) {
            return new FriendReceiveRequest();
        }
        return instance;
    }
}
