package me.fodded.bungeecord.commands;

import me.fodded.bungeecord.serverhandlers.servers.LobbyServerInfoHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LobbyCommand extends Command {
    public LobbyCommand() {
        super("lobby", "", "l", "leave");
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;

            LobbyServerInfoHandler lobbyServerInfoHandler = new LobbyServerInfoHandler();
            lobbyServerInfoHandler.clearAndUpdateServerInformation();

            String currentServerName = player.getServer().getInfo().getName();
            ServerInfo server = lobbyServerInfoHandler.getSuitableServer(currentServerName);

            if(args.length == 1) {
                String serverNamePattern = args[0];
                server = lobbyServerInfoHandler.getSuitableServer(player.getUniqueId(), currentServerName, serverNamePattern);
            }

            player.connect(server);
        }
    }
}