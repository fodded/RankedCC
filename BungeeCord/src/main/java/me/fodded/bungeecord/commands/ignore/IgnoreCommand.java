package me.fodded.bungeecord.commands.ignore;

import me.fodded.bungeecord.managers.FriendManager;
import me.fodded.bungeecord.managers.IgnoreManager;
import me.fodded.bungeecord.managers.LanguageManager;
import me.fodded.bungeecord.managers.friends.FriendAcceptRequest;
import me.fodded.bungeecord.managers.friends.FriendDeclineRequest;
import me.fodded.bungeecord.managers.friends.FriendSendRequest;
import me.fodded.bungeecord.managers.ignore.IgnoreAddRequest;
import me.fodded.bungeecord.managers.ignore.IgnoreRemoveRequest;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class IgnoreCommand extends Command {
    public IgnoreCommand() {
        super("ignore");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            if(args.length == 0) {
                sendFriendCommandsHelp(player);
            }

            if(args.length == 1) {
                IgnoreAddRequest.getInstance().process(player, args[0]);
                return;
            }

            if(args.length == 2) {
                switch(args[0]) {
                    case "remove":
                        IgnoreRemoveRequest.getInstance().process(player, args[1]);
                        break;
                    case "add":
                        IgnoreAddRequest.getInstance().process(player, args[1]);
                        break;
                }
            }
        }
    }

    private void sendFriendCommandsHelp(ProxiedPlayer player) {
        StringUtils.sendMessage(
                player,
                LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getStringList("ignore.ignore-command-help")
        );
    }
}