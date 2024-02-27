package me.fodded.bungeecord.commands;

import me.fodded.bungeecord.serverhandlers.BungeeServerHandler;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.ranks.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WhitelistCommand extends Command {
    public WhitelistCommand() {
        super("turn");
    }

    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (!Rank.hasPermission(Rank.ADMIN, player.getUniqueId())) {
                return;
            }
        }

        if(args.length == 0) {
            boolean isWhitelisted = BungeeServerHandler.getInstance().isServerWhitelisted();
            BungeeServerHandler.getInstance().setServerWhitelisted(!isWhitelisted);

            sender.sendMessage(StringUtils.format("&fNow whitelist is " + (isWhitelisted ? "&coff" : "&aon")));
            return;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("add")) {
                BungeeServerHandler.getInstance().addToBungeeWhiteList(args[1]);
                sender.sendMessage(StringUtils.format("&aSuccessfully added " + args[1] + " to the whitelist"));
            }

            if(args[0].equalsIgnoreCase("remove")) {
                BungeeServerHandler.getInstance().removeFromBungeeWhiteList(args[1]);
                sender.sendMessage(StringUtils.format("&aSuccessfully removed " + args[1] + " from the whitelist"));
            }
        }
    }
}