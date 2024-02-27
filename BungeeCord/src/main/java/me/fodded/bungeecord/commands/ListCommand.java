package me.fodded.bungeecord.commands;

import me.fodded.bungeecord.Main;
import me.fodded.bungeecord.utils.StringUtils;
import me.fodded.core.managers.ranks.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class ListCommand extends Command {
    public ListCommand() {
        super("list");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;

            if(!Rank.hasPermission(Rank.ADMIN, player.getUniqueId())) {
                player.sendMessage(StringUtils.format("&cYou do not have permissions to use this command!"));
                return;
            }

            player.sendMessage(StringUtils.format("Totally players: " + Main.getInstance().getProxy().getPlayers().size()));
            for(Map.Entry entry : Main.getInstance().getProxy().getServers().entrySet()) {
                ServerInfo serverInfo = (ServerInfo) entry.getValue();
                player.sendMessage(serverInfo.getName() + ": " + serverInfo.getPlayers().size());
            }
        }
    }
}