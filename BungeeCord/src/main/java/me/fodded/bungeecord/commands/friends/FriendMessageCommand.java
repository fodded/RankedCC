package me.fodded.bungeecord.commands.friends;

import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FriendMessageCommand extends Command {
    public FriendMessageCommand() {
        super("tell", "", "msg", "message", "say", "whisper", "w", "pm", "dm");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;

            if(args.length < 2) {
                player.sendMessage(StringUtils.format("&c/msg [player] [message]"));
                return;
            }

            String targetPlayerName = args[0];
            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
            if(targetPlayer == null) {
                StringUtils.sendMessage(player, "friends.no-such-player");
                return;
            }

            GeneralStats targetPlayerGeneralStats = GeneralStatsDataManager.getInstance().getCachedValue(targetPlayer.getUniqueId());
            if(targetPlayerGeneralStats.getIgnoreList().contains(player.getUniqueId()) && !Rank.hasPermission(Rank.HELPER, player.getUniqueId())) {
                StringUtils.sendMessage(player, "friends.cant-send-message");
                return;
            }

            if(targetPlayerGeneralStats.isVanished()) {
                StringUtils.sendMessage(player, "friends.no-player-present");
                return;
            }

            boolean isPlayerBypassingSettings = Rank.hasPermission(Rank.YOUTUBE, player.getUniqueId());
            if(isPlayerBypassingSettings) {
                sendMessage(player, targetPlayer, args);
                return;
            }

            if(targetPlayerGeneralStats.getFriendList().contains(player.getUniqueId()) || targetPlayerGeneralStats.isPrivateMessagesEnabled()) {
                sendMessage(player, targetPlayer, args);
                return;
            }

            StringUtils.sendMessage(player, "friends.cant-send-message");
        }
    }

    private void sendMessage(ProxiedPlayer player, ProxiedPlayer targetPlayer, String[] args) {
        String message = getMessageFromArray(args);

        String senderPrefix = StringUtils.getPlayerPrefix(player);
        String receiverPrefix = StringUtils.getPlayerPrefix(targetPlayer);

        targetPlayer.sendMessage(StringUtils.formatString("&6&lFROM " + senderPrefix + "&f: ") + message);
        player.sendMessage(StringUtils.formatString("&6&lTO " + receiverPrefix + "&f: ") + message);
    }

    private String getMessageFromArray(String[] args) {
        String text = "";
        for(int i = 1; i < args.length; i++) {
            text += args[i];
        }
        return text;
    }
}