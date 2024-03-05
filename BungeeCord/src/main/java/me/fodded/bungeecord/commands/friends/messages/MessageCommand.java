package me.fodded.bungeecord.commands.friends.messages;

import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MessageCommand extends Command {
    public MessageCommand() {
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

            String message = StringUtils.getMessageFromArray(args, 1);
            StringUtils.sendPrivateMessage(message, player, targetPlayer, targetPlayerGeneralStats);
        }
    }
}