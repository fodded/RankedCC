package me.fodded.bungeecord.commands.ignore;

import me.fodded.bungeecord.managers.LanguageManager;
import me.fodded.bungeecord.managers.ignore.IgnoreAddRequest;
import me.fodded.bungeecord.managers.ignore.IgnoreRemoveRequest;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class IgnoreCommand extends Command {
    public IgnoreCommand() {
        super("ignore");
    }

    private static final int IGNORED_PER_PAGE = 10;

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            if(args.length == 0) {
                sendIgnoreCommandsHelp(player);
            }

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    sendIgnoredPlayersList(sender, args);
                    return;
                }
                IgnoreAddRequest.getInstance().process(player, args[0]);
                return;
            }

            if(args.length == 2) {
                switch(args[0]) {
                    case "list":
                        sendIgnoredPlayersList(sender, args);
                        break;
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

    private void sendIgnoreCommandsHelp(ProxiedPlayer player) {
        StringUtils.sendMessage(
                player,
                LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getStringList("ignore.ignore-command-help")
        );
    }

    public void sendIgnoredPlayersList(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        CompletableFuture.runAsync(() -> {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());

            List<UUID> ignoredList = generalStats.getIgnoreList();

            int maxPages = getMaxPages(ignoredList.size());
            int page = getPage(args, ignoredList.size());

            List<String> ignoredMessageList = getIgnoredPlayersList(page, ignoredList);
            sendIgnoredPlayersPageMessage(player, ignoredMessageList, page, maxPages);
        });
    }

    private void sendIgnoredPlayersPageMessage(ProxiedPlayer player, List<String> ignoredPlayersMessageList, int currentPage, int maxPages) {
        List<String> ignoredListMessages = LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getStringList("ignore.ignore-list");
        for(String message : ignoredListMessages) {
            if(message.equalsIgnoreCase("%ignored%")) {
                StringUtils.sendMessage(player, ignoredPlayersMessageList);
                continue;
            }

            message = message.replace("%current_page%", String.valueOf(currentPage+1))
                    .replace("%pages%", String.valueOf(maxPages+1));

            player.sendMessage(StringUtils.format(message));
        }
    }

    private List<String> getIgnoredPlayersList(int page, List<UUID> friendList) {
        List<String> ignoredPlayersMessageList = new LinkedList<>();
        int startIndex = page * IGNORED_PER_PAGE;
        int endIndex = Math.min(startIndex + IGNORED_PER_PAGE, friendList.size());

        for(int index = startIndex; index < endIndex; index++) {
            UUID ignoredPlayerUniqueId = friendList.get(index);

            String prefix = StringUtils.getPlayerPrefix(ignoredPlayerUniqueId);
            ignoredPlayersMessageList.add("&f#" + (index+1) + " " + prefix + " ");
        }
        return ignoredPlayersMessageList;
    }

    private int getPage(String[] args, int arraySize) {
        if (args.length != 2) return 0;

        // We do not want to get negative pages from a player input
        // And we do not want to get a page higher than max pages
        int page = Math.max(0, Integer.parseInt(args[0]) - 1);
        return Math.min(getMaxPages(arraySize), page);
    }

    private int getMaxPages(int arrSize) {
        return Math.round((float) arrSize / IGNORED_PER_PAGE);
    }
}