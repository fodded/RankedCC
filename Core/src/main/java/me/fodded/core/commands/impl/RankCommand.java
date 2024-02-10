package me.fodded.core.commands.impl;

import me.fodded.core.commands.CommandInfo;
import me.fodded.core.commands.PluginCommand;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.ranks.RankManager;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.managers.stats.impl.GeneralStats;
import me.fodded.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandInfo(rank = RankType.ADMIN, name = "rank", usage = "/rank [player] set/info", description = "Manage ranks")
public class RankCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(getUsage());
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(StringUtils.format("&cThe player " + args[0] + " has never played on the server before"));
            return;
        }

        GeneralStats generalStats = new GeneralStats().getStatistics(offlinePlayer.getUniqueId());
        switch(args[1]) {
            case "set":
                if(args.length < 3) {
                    sender.sendMessage(StringUtils.format("&c/rank [player] set [rank]"));
                    return;
                }

                RankType rankToGive = RankType.valueOf(args[2].toUpperCase());
                sender.sendMessage(StringUtils.format("&aYou've given rank " + rankToGive + " to " + offlinePlayer.getName()));

                generalStats.setRank(rankToGive);
                break;
            case "info":
                Rank rank = RankManager.getInstance().getRank(generalStats.getRank());

                sender.sendMessage(StringUtils.format("&7"));
                sender.sendMessage(StringUtils.format("&f&lPLAYER'S RANK INFORMATION"));
                sender.sendMessage(StringUtils.format(rank.getPrefix() + generalStats.getName()));
                sender.sendMessage(StringUtils.format("&7"));
                break;
        }
    }
}
