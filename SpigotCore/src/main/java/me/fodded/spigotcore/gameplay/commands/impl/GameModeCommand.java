package me.fodded.spigotcore.gameplay.commands.impl;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(rank = Rank.ADMIN, name = "gm", usage = "&c/gm [1-3]", description = "shortened gamemode command")
public class GameModeCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length != 1) {
            player.sendMessage(StringUtils.format(getUsage()));
            return;
        }

        int gamemode = Integer.parseInt(args[0]);
        if(gamemode < 0 || gamemode > 3) {
            player.sendMessage(StringUtils.format(getUsage()));
            return;
        }

        player.setGameMode(GameMode.getByValue(gamemode));
    }
}
