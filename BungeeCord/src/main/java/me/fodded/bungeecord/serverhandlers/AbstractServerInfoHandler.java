package me.fodded.bungeecord.serverhandlers;

import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;

@Data
public abstract class AbstractServerInfoHandler {

    private final String serverName;
    private final int playersOnline;

    public AbstractServerInfoHandler(String serverName, int playersOnline) {
        this.serverName = serverName;
        this.playersOnline = playersOnline;
    }

    public AbstractServerInfoHandler() {
        this.serverName = "";
        this.playersOnline = 0;
    }

    public ServerInfo getProxyServer(String serverName) {
        for (Map.Entry entry : ProxyServer.getInstance().getServers().entrySet()) {
            ServerInfo proxyServer = (ServerInfo) entry.getValue();
            if(proxyServer.getName().equalsIgnoreCase(serverName)) {
                return proxyServer;
            }
        }

        System.out.println("For some reason proxy server wasn't found out of " + AbstractServerInfoHandler.class.getSimpleName() + " | " + serverName);
        return null;
    }

    public abstract ServerInfo getSuitableServer(String information);
    public abstract void clearAndUpdateServerInformation();
}
