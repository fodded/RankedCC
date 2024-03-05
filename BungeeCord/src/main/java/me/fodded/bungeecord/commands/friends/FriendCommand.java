package me.fodded.bungeecord.commands.friends;

import me.fodded.bungeecord.managers.LanguageManager;
import me.fodded.bungeecord.managers.friends.FriendAcceptRequest;
import me.fodded.bungeecord.managers.friends.FriendDeclineRequest;
import me.fodded.bungeecord.managers.friends.FriendManager;
import me.fodded.bungeecord.managers.friends.FriendSendRequest;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend", "", "f");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            if(args.length == 0) {
                sendFriendCommandsHelp(player);
            }

            if(args.length == 1) {
                proceedFriendRequest(player, args[0]);
                return;
            }

            if(args.length == 2) {
                switch(args[0]) {
                    case "remove":
                        proceedFriendRemove(player, args[1]);
                        break;
                    case "add":
                        proceedFriendRequest(player, args[1]);
                        break;
                    case "accept":
                        acceptFriendRequest(player, args[1]);
                        break;
                    case "decline":
                        declineFriendRequest(player, args[1]);
                        break;
                }
            }
        }
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

        FriendManager friendManager = FriendManager.getInstance();
        friendManager.removeFromFriends(playerSentRequest.getUniqueId(), playerReceivedRequestUUID);
        friendManager.removeFromFriends(playerReceivedRequestUUID, playerSentRequest.getUniqueId());

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

    public void declineFriendRequest(ProxiedPlayer playerReceivedRequest, String playerSentRequestName) {
        ProxiedPlayer playerSentRequest = ProxyServer.getInstance().getPlayer(playerSentRequestName);
        if(playerSentRequest == null) {
            StringUtils.sendMessage(playerReceivedRequest, "friends.no-player-present");
            return;
        }

        FriendDeclineRequest.getInstance().processRequest(playerSentRequest, playerReceivedRequest);
    }

    private void acceptFriendRequest(ProxiedPlayer playerReceivedRequest, String playerSentRequestName) {
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

    private void sendFriendCommandsHelp(ProxiedPlayer player) {
        StringUtils.sendMessage(
                player,
                LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getStringList("friends.friend-command-help")
        );
    }
}