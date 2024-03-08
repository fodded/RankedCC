package me.fodded.bungeecord.commands.bans;

import me.fodded.bungeecord.managers.punishments.bans.UnbanProcess;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.operators.DatabaseOperations;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class UnbanCommand extends Command {
    public UnbanCommand() {
        super("unban");
    }

    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            if(!Rank.hasPermission(Rank.ADMIN, ((ProxiedPlayer) sender).getUniqueId())) {
                return;
            }
        }

        if(args.length != 1) {
            sender.sendMessage(StringUtils.format("&c/unban [player]"));
            return;
        }

        String bannedPlayerName = args[0];
        DatabaseOperations databaseOperations = DatabaseOperations.getInstance();

        UUID bannedPlayerUniqueId = databaseOperations.getUniqueIdFromName(bannedPlayerName);
        if(bannedPlayerUniqueId == null) {
            sender.sendMessage(StringUtils.format("&cCould not find player " + bannedPlayerName));
            return;
        }

        System.out.println(bannedPlayerUniqueId + " banned player uuid");

        boolean isPlayerAlreadyBanned = databaseOperations.isPlayerCurrentlyBanned(bannedPlayerUniqueId);
        if(!isPlayerAlreadyBanned) {
            sender.sendMessage(StringUtils.format("&cThe player is not banned"));
            return;
        }

        String staffExecutedCommand = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId().toString() : "CONSOLE";
        UnbanProcess.getInstance().process(staffExecutedCommand, bannedPlayerUniqueId);
        sender.sendMessage(StringUtils.format("&aYou've successfully unbanned " + bannedPlayerName));
    }
}
