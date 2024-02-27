package me.fodded.spigotcore.gameplay.commands.impl;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(rank = Rank.YOUTUBE, name = "nick", usage = "&c/nick [player] [rank]", description = "Hides your original name")
public class NickCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reset")) {
                resetDisguisedNick(player);
                return;
            }
        }

        if(args.length != 2) {
            sender.sendMessage(StringUtils.format(getUsage()));
            return;
        }

        setDisguisedNick(player, args);
    }

    private void setDisguisedNick(Player player, String[] args) {
        String disguisedNick = args[0];
        Rank disguisedRank = Rank.getRank(args[1]);
        if(disguisedRank == null) {
            player.sendMessage(StringUtils.format("&cCouldn't find rank " + args[1]));
            return;
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());
        if(disguisedRank.getPriority() > generalStats.getRank().getPriority()) {
            player.sendMessage(StringUtils.format("&cYou can not set disguised rank higher than you have right now"));
            return;
        }

        DisguiseManager.getInstance().setDisguise(player, disguisedNick);
        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), updatedGeneralStats -> updatedGeneralStats.setDisguisedRank(disguisedRank));
    }

    private void resetDisguisedNick(Player player) {
        DisguiseManager disguiseManager = DisguiseManager.getInstance();
        disguiseManager.setDisguise(player, disguiseManager.getNameFromUUID(player.getUniqueId()));

        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), updatedGeneralStats -> updatedGeneralStats.setDisguisedName(""));
    }
}

