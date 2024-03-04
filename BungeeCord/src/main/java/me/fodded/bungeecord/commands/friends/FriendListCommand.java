package me.fodded.bungeecord.commands.friends;

import me.fodded.bungeecord.managers.LanguageManager;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendListCommand extends Command {
    public FriendListCommand() {
        super("friendlist", "", "fl");
    }

    private static final int FRIENDS_PER_PAGE = 10;
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        CompletableFuture.runAsync(() -> {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());

            List<UUID> friendList = generalStats.getFriendList();

            int maxPages = getMaxPages(friendList.size());
            int page = getPage(args, friendList.size());

            List<String> friendsMessageList = getFriendsList(page, friendList);
            sendFriendPageMessage(player, friendsMessageList, page, maxPages);
        });
    }

    private void sendFriendPageMessage(ProxiedPlayer player, List<String> friendsMessageList, int currentPage, int maxPages) {
        List<String> friendListMessages = LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getStringList("friends.friend-list");
        for(String message : friendListMessages) {
            if(message.equalsIgnoreCase("%friends%")) {
                StringUtils.sendMessage(player, friendsMessageList);
                continue;
            }

            message = message.replace("%current_page%", String.valueOf(currentPage+1))
                    .replace("%pages%", String.valueOf(maxPages+1));

            player.sendMessage(StringUtils.format(message));
        }
    }

    private List<String> getFriendsList(int page, List<UUID> friendList) {
        List<String> friendMessageList = new LinkedList<>();
        int startIndex = page * FRIENDS_PER_PAGE;
        int endIndex = Math.min(startIndex + FRIENDS_PER_PAGE, friendList.size());

        for(int index = startIndex; index < endIndex; index++) {
            UUID friendUniqueId = friendList.get(index);
            ProxiedPlayer friendPlayer = ProxyServer.getInstance().getPlayer(friendUniqueId);

            boolean isFriendOnline = friendPlayer != null;
            String prefix = StringUtils.getPlayerPrefix(friendUniqueId);

            friendMessageList.add("&f#" + (index+1) + " " + prefix + " " + (isFriendOnline ? "&a&lOnline " : "&c&lOffline"));
        }
        return friendMessageList;
    }

    private int getPage(String[] args, int friendsListSize) {
        if (args.length != 1) return 0;

        // We do not want to get negative pages from a player input
        // And we do not want to get a page higher than max pages
        int page = Math.max(0, Integer.parseInt(args[0]) - 1);
        return Math.min(getMaxPages(friendsListSize), page);
    }

    private int getMaxPages(int friendsListSize) {
        return Math.round((float) friendsListSize / FRIENDS_PER_PAGE);
    }
}