package me.fodded.spigotcore.gameplay.commands.impl;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.gameplay.disguise.DisguiseManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
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

        if(disguisedNick.length() < 3 || disguisedNick.length() > 16 || doesContainIllegalCharacters(disguisedNick)) {
            StringUtils.sendMessage(player, "disguise.nick-not-allowed");
            return;
        }

        if(disguisedRank == null) {
            StringUtils.sendMessage(player, "no-rank-found", args[1]);
            return;
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(player.getUniqueId());
        if(disguisedRank.getPriority() > generalStats.getRank().getPriority()) {
            StringUtils.sendMessage(player, "disguise.rank-higher");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(SpigotCore.getInstance().getPlugin(), () -> {
            if(DatabaseOperations.getInstance().doesCollectionHaveFieldValue("GeneralStats", "lastName", disguisedNick)) {
                StringUtils.sendMessage(player, "disguise.player-exists");
                return;
            }

            Bukkit.getScheduler().runTask(SpigotCore.getInstance().getPlugin(), () -> {
                DisguiseManager.getInstance().setDisguise(player, disguisedNick);
                GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), updatedGeneralStats -> updatedGeneralStats.setDisguisedRank(disguisedRank));
            });
        });
    }

    private void resetDisguisedNick(Player player) {
        DisguiseManager disguiseManager = DisguiseManager.getInstance();
        disguiseManager.setDisguise(player, disguiseManager.getNameFromUUID(player.getUniqueId()));

        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), updatedGeneralStats -> updatedGeneralStats.setDisguisedName(""));
    }

    public boolean doesContainIllegalCharacters(String input) {
        String legalCharactersRegex = "^[a-zA-Z0-9_]*$";
        return !input.matches(legalCharactersRegex);
    }
}

