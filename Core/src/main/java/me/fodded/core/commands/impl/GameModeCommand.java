package me.fodded.core.commands.impl;

import me.fodded.core.commands.CommandInfo;
import me.fodded.core.commands.PluginCommand;
import me.fodded.core.managers.ranks.RankType;
import me.fodded.core.utils.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(rank = RankType.ADMIN, name = "gm", usage = "/gm [1-3]", description = "shortened gamemode command")
public class GameModeCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length != 1) {
            player.sendMessage(StringUtils.format("&c" + getUsage()));
            return;
        }

        int gamemode = Integer.parseInt(args[0]);
        if(gamemode < 0 || gamemode > 3) {
            player.sendMessage(StringUtils.format("&c" + getUsage()));
            return;
        }

        player.setGameMode(GameMode.getByValue(gamemode));
    }
}
