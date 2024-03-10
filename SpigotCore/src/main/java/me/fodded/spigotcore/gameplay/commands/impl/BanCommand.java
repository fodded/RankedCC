package me.fodded.spigotcore.gameplay.commands.impl;

import me.fodded.core.Core;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.redisson.api.RTopic;

import java.util.concurrent.CompletableFuture;

@CommandInfo(name = "ban", usage = "/ban [player]", description = "bans player", rank = Rank.MODERATOR)
public class BanCommand extends PluginCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length != 1) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            RTopic topic = Core.getInstance().getRedis().getRedissonClient().getTopic("banPlayer");

            String staffName = sender instanceof Player ? ((Player) sender).getUniqueId().toString() : "CONSOLE";
            topic.publish(staffName + ":" + args[0]);
        });
    }
}