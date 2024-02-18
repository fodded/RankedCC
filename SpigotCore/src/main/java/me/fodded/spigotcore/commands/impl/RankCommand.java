package me.fodded.spigotcore.commands.impl;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.commands.CommandInfo;
import me.fodded.spigotcore.commands.PluginCommand;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CommandInfo(rank = Rank.ADMIN, name = "rank", usage = "/rank [player] set [rank]\n/rank list\n/rank [player] info", description = "Manage ranks")
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

        switch (args[1]) {
            case "set":
                setPlayerRank(sender, args, offlinePlayer.getUniqueId());
                break;
            case "info":
                printRankInformation(sender, offlinePlayer.getUniqueId());
                break;
        }
    }

    private void printRankInformation(CommandSender sender, UUID uniqueId) {
        CompletableFuture.runAsync(() -> {
            GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
            GeneralStats generalStats = generalStatsDataManager.getCachedValue(uniqueId);

            Rank rank = generalStats.getRank();

            sender.sendMessage(StringUtils.format("&7"));
            sender.sendMessage(StringUtils.format("&f&lPLAYER'S RANK INFORMATION"));
            sender.sendMessage(StringUtils.format(rank.getPrefix() + generalStats.getLastName()));
            sender.sendMessage(StringUtils.format("&7"));
        });
    }

    private void setPlayerRank(CommandSender sender, String[] args, UUID uniqueId) {
        if(args.length < 3) {
            sender.sendMessage(StringUtils.format("&c/rank [player] set [rank]"));
            return;
        }

        Rank rankToGive = Rank.valueOf(args[2].toUpperCase());
        CompletableFuture.runAsync(() -> {
            GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
            generalStatsDataManager.applyChange(uniqueId, generalStats -> generalStats.setRank(rankToGive));

            GeneralStats generalStats = generalStatsDataManager.getRemoteValue(uniqueId);
            sender.sendMessage(StringUtils.format("&aYou've successfully given rank " + rankToGive + " &ato " + generalStats.getLastName()));
        });
    }

    private void printRankList(CommandSender sender) {
        sender.sendMessage(StringUtils.format("&7"));
        sender.sendMessage(StringUtils.format("&f&lLIST OF RANKS"));
        for(Rank rank : Rank.values()) {
            sender.sendMessage(StringUtils.format(rank.getPrefix() + rank.name() + " &7Priority: " + rank.getPriority()));
        }
        sender.sendMessage(StringUtils.format("&7"));
    }
}
