package me.fodded.spigotcore.commands.impl;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.model.DataManager;
import me.fodded.core.model.GlobalDataManager;
import me.fodded.spigotcore.commands.CommandInfo;
import me.fodded.spigotcore.commands.PluginCommand;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@CommandInfo(rank = Rank.ADMIN, name = "stats", usage = "/stats [player] [minigame] set [statistic] [value]", description = "set statistics")
public class StatsCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length != 5) {
            sender.sendMessage(StringUtils.format(getUsage()));
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if(!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(StringUtils.format("&cCould not find player " + args[0]));
            return;
        }

        GlobalDataManager dataManager = getDataManager(args[1]);
        if(dataManager == null) {
            sender.sendMessage(StringUtils.format("&cCouldn't find Minigame called " + args[1] + " (for example SkywarsStats)"));
            return;
        }


        sender.sendMessage(StringUtils.format("&aYou've successfully updated " + args[3] + " to " + args[4] + "&a for " + offlinePlayer.getName()));

    }

    private GlobalDataManager getDataManager(String dataName) {
        for(GlobalDataManager dataManager : DataManager.getInstance().getStatisticsList()) {
            if(dataManager.getClass().getSimpleName().substring(0, 11).equalsIgnoreCase(dataName)) {
                return dataManager;
            }
        }
        return null;
    }
}