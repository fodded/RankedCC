package me.fodded.bungeecord.commands.friends;

import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.UUID;

public class FriendListCommand extends Command {
    public FriendListCommand() {
        super("friendlist", "", "fl");
    }

    private static final int FRIENDS_PER_PAGE = 10;
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;

            int page = 0;
            if(args.length == 1) {
                page = Integer.parseInt(args[0]) - 1;
                if(page < 0) {
                    page = 0;
                }

                int maxPages = 1000 / FRIENDS_PER_PAGE;
                if(page > maxPages) {
                    page = maxPages;
                }
            }

            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());
            List<UUID> friendList = generalStats.getFriendList();
            for(int i = page * FRIENDS_PER_PAGE; i < page * FRIENDS_PER_PAGE + FRIENDS_PER_PAGE; i++) {
                if(friendList.size() < i+1) {
                    return;
                }

                UUID friendUniqueId = friendList.get(i);
                ProxiedPlayer friendPlayer = ProxyServer.getInstance().getPlayer(friendUniqueId);

                boolean isFriendOnline = friendPlayer != null;
                String prefix = StringUtils.getPlayerPrefix(friendUniqueId);

                player.sendMessage(StringUtils.format(
                        "&f#" + (i+1) + " " + prefix + " " + (isFriendOnline ? "&a&lOnline " : "&c&lOffline"))
                );
            }
        }
    }
}