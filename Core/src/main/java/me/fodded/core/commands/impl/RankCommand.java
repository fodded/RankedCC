package me.fodded.core.commands.impl;

import me.fodded.core.commands.CommandInfo;
import me.fodded.core.commands.PluginCommand;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.ranks.RankManager;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import me.fodded.core.managers.stats.loaders.RedisLoader;
import me.fodded.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

@CommandInfo(rank = RankType.ADMIN, name = "rank", usage = "/rank [player] set [rank]\n/rank list\n/rank [player] info", description = "Manage ranks")
public class RankCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 1) {
            sender.sendMessage(StringUtils.format("&7"));
            sender.sendMessage(StringUtils.format("&f&lRANK COMMAND INFORMATION"));
            for(String text : getUsage().split("\n")) {
                sender.sendMessage(StringUtils.format(text));
            }
            sender.sendMessage(StringUtils.format("&7"));
            return;
        }

        if(args[0].equalsIgnoreCase("list")) {
            printRankList(sender);
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

        // Check if player is online and playing for the first time
        if(!offlinePlayer.hasPlayedBefore()) {
            if(player == null) {
                sender.sendMessage(StringUtils.format("&cThe player " + args[0] + " has never played on the server before"));
                return;
            }
        }

        // We need to know it to get date either from redis or from database
        boolean isPlayerOnline;
        if(player != null) {
            isPlayerOnline = true;
        } else {
            isPlayerOnline = false;
        }

        ForkJoinPool.commonPool().submit(() -> {
            switch (args[1]) {
                case "set":
                    setPlayerRank(sender, args, offlinePlayer.getUniqueId(), isPlayerOnline);
                    break;
                case "info":
                    printRankInformation(sender, offlinePlayer.getUniqueId(), isPlayerOnline);
                    break;
            }
        });
    }

    private void printRankInformation(CommandSender sender, UUID uniqueId, boolean isPlayerOnline) {
        GeneralStats generalStats = new GeneralStats().getStatistics(uniqueId, isPlayerOnline);
        Rank rank = RankManager.getInstance().getRank(generalStats.getRank());

        sender.sendMessage(StringUtils.format("&7"));
        sender.sendMessage(StringUtils.format("&f&lPLAYER'S RANK INFORMATION"));
        sender.sendMessage(StringUtils.format(rank.getPrefix() + generalStats.getName()));
        sender.sendMessage(StringUtils.format("&7"));
    }

    private void setPlayerRank(CommandSender sender, String[] args, UUID uniqueId, boolean isPlayerOnline) {
        if(args.length < 3) {
            sender.sendMessage(StringUtils.format("&c/rank [player] set [rank]"));
            return;
        }

        GeneralStats generalStats = new GeneralStats().getStatistics(uniqueId, isPlayerOnline);
        RankType rankToGive = RankType.valueOf(args[2].toUpperCase());

        sender.sendMessage(StringUtils.format("&aYou've successfully given rank " + rankToGive + " &ato " + generalStats.getName()));
        generalStats.setRank(rankToGive);

        if(isPlayerOnline) {
            RedisLoader.getInstance().uploadStatistics(uniqueId, generalStats);
            return;
        }
        DatabaseLoader.getInstance().uploadStatistics(uniqueId, generalStats);
    }

    private void printRankList(CommandSender sender) {
        sender.sendMessage(StringUtils.format("&7"));
        sender.sendMessage(StringUtils.format("&f&lLIST OF RANKS"));
        for(Rank rank : RankManager.getInstance().getRanksList()) {
            sender.sendMessage(StringUtils.format(rank.getPrefix() + rank.getRank().name() + " &7Priority: " + rank.getPriority()));
        }
        sender.sendMessage(StringUtils.format("&7"));
    }
}
