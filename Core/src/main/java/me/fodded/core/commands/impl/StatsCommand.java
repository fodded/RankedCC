package me.fodded.core.commands.impl;

import me.fodded.core.commands.CommandInfo;
import me.fodded.core.commands.PluginCommand;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.managers.stats.Database;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import me.fodded.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;

@CommandInfo(rank = RankType.ADMIN, name = "stats", usage = "/stats [player] Minigame set Statistic Value", description = "set statistics")
public class StatsCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 5) {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null) {
                sender.sendMessage(StringUtils.format("&cYou can't change statistics of an online player"));
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

            if(!DatabaseOperations.getInstance().collectionExists(args[1])) {
                sender.sendMessage(StringUtils.format("&cCouldn't find Minigame called " + args[1] + " (The names are case sensetive, for example SkywarsStats)"));
                return;
            }

            if(!DatabaseOperations.getInstance().statisticExists(offlinePlayer.getUniqueId(), args[1], args[3])) {
                sender.sendMessage(StringUtils.format("&cCouldn't find Statistic called " + args[3] + " (The names are case sensetive, for example kills)"));
                return;
            }

            DatabaseOperations.getInstance().updateStatistic(offlinePlayer.getUniqueId(), args[1], args[3], args[4]);
            sender.sendMessage(StringUtils.format("&aYou've successfully updated " + args[3] + " to " + args[4] + " for " + offlinePlayer.getName()));
            return;
        }

        sender.sendMessage(StringUtils.format(getUsage()));
    }
}