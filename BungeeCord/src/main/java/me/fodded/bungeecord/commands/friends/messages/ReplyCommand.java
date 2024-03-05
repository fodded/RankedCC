package me.fodded.bungeecord.commands.friends.messages;

import me.fodded.bungeecord.managers.friends.FriendManager;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ReplyCommand extends Command {
    public ReplyCommand() {
        super("reply", "", "r");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            if(args.length < 1) {
                player.sendMessage(StringUtils.format("&c/reply [message]"));
                return;
            }

            UUID lastPlayerSentMessageUniqueId = FriendManager.getInstance().getLastReceivedMessageFrom(player.getUniqueId());
            ProxiedPlayer lastPlayerSentMessage = ProxyServer.getInstance().getPlayer(lastPlayerSentMessageUniqueId);

            if(lastPlayerSentMessageUniqueId == null || lastPlayerSentMessage == null) {
                StringUtils.sendMessage(player, "friends.no-player-present");
                return;
            }

            GeneralStats targetPlayerGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(lastPlayerSentMessageUniqueId);

            String message = StringUtils.getMessageFromArray(args, 0);
            StringUtils.sendPrivateMessage(message, player, lastPlayerSentMessage, targetPlayerGeneralStats);
        }
    }
}