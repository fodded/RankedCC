package me.fodded.core.commands.impl;

import me.fodded.core.commands.CommandInfo;
import me.fodded.core.commands.PluginCommand;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import me.fodded.core.managers.stats.operators.RedisOperations;
import me.fodded.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandInfo(rank = RankType.ADMIN, name = "stats", usage = "/stats [player] [minigame] set [statistic] [value]", description = "set statistics")
public class StatsCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 5) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if(!DatabaseOperations.getInstance().collectionExists(args[1])) {
                sender.sendMessage(StringUtils.format("&cCouldn't find Minigame called " + args[1] + " (The names are case sensetive, for example SkywarsStats)"));
                return;
            }

            if(!DatabaseOperations.getInstance().statisticExists(offlinePlayer.getUniqueId(), args[1], args[3])) {
                sender.sendMessage(StringUtils.format("&cCouldn't find Statistic called " + args[3] + " (The names are case sensetive, for example kills)"));
                return;
            }

            // If the player's data is present in redis then we only edit it.
            // If not, then we edit data directly in the database
            String key = args[1] + ":" + offlinePlayer.getUniqueId().toString();
            if(RedisOperations.getInstance().isKeyPresent(key)) {
                key += ":" + args[3] + ":" + args[4];
                RedisOperations.getInstance().publishData("editData", key);
                sender.sendMessage(StringUtils.format("&aYou've successfully updated " + args[3] + " to " + args[4] + " for " + offlinePlayer.getName()));
                return;
            }

            DatabaseOperations.getInstance().updateStatistic(offlinePlayer.getUniqueId(), args[1], args[3], args[4]);
            sender.sendMessage(StringUtils.format("&aYou've successfully updated " + args[3] + " to " + args[4] + " for " + offlinePlayer.getName()));
            return;
        }

        sender.sendMessage(StringUtils.format(getUsage()));
    }
}