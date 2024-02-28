package me.fodded.bungeecord;

import lombok.Getter;
import me.fodded.bungeecord.commands.ListCommand;
import me.fodded.bungeecord.commands.LobbyCommand;
import me.fodded.bungeecord.commands.WhitelistCommand;
import me.fodded.bungeecord.listeners.PlayerConnectListener;
import me.fodded.bungeecord.redislisteners.SendLogsToPlayerListener;
import me.fodded.bungeecord.redislisteners.SendPlayerToLobbyListener;
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

        registerRedisListeners();
        registerRedisListeners();

        // Registering custom bungee listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerConnectListener());
    }

    private void registerBungeeCommands() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ListCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LobbyCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new WhitelistCommand());
    }

    private void registerRedisListeners() {
        RTopic sendPlayerToLobbyTopic = Core.getInstance().getRedis().getRedissonClient().getTopic("sendPlayerToLobby");
        sendPlayerToLobbyTopic.addListener(String.class, (channel, msg) -> {
            new SendPlayerToLobbyListener().onMessage(channel, msg);
        });

        RTopic sendLogsToPlayer = Core.getInstance().getRedis().getRedissonClient().getTopic("sendLogsToPlayer");
        sendLogsToPlayer.addListener(String.class, (channel, msg) -> {
            new SendLogsToPlayerListener().onMessage(channel, msg);
        });
    }

    @Override
    public void onDisable() {

    }
}