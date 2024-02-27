package me.fodded.bungeecord;

import lombok.Getter;
import me.fodded.bungeecord.commands.ListCommand;
import me.fodded.bungeecord.commands.LobbyCommand;
import me.fodded.bungeecord.commands.WhitelistCommand;
import me.fodded.bungeecord.listeners.PlayerConnectListener;
import me.fodded.core.Core;
import me.fodded.proxycore.ProxyCore;
import me.fodded.proxycore.configs.ConfigLoader;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.redisson.api.RTopic;

public class Main extends Plugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        ProxyCore.initialize(instance);
        ProxyCore.getInstance().initializeListeners();
        ConfigLoader.getInstance().createConfig();

        Configuration config = ConfigLoader.getInstance().getConfig();
        ProxyCore.getInstance().initializeRedis(config);
        ProxyCore.getInstance().initializeDatabase(config);

        // Registering redis listeners
        RTopic sendPlayerToLobbyTopic = Core.getInstance().getRedis().getRedissonClient().getTopic("sendPlayerToLobby");
        sendPlayerToLobbyTopic.addListener(String.class, (channel, msg) -> {
            new BungeeRedisListener().onMessage(channel, msg);
        });

        // Registering commands
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ListCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LobbyCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new WhitelistCommand());

        // Registering custom bungee listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerConnectListener());
    }

    @Override
    public void onDisable() {

    }
}