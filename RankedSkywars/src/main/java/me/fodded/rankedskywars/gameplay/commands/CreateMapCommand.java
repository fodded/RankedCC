package me.fodded.rankedskywars.gameplay.commands;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "setlobby", usage = "/setlobby", description = "set new lobby location", rank = Rank.ADMIN)
public class CreateMapCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            return;
        }

        if(args.length == 1) {

            return;
        }


    }
}
