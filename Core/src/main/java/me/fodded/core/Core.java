package me.fodded.core;

import lombok.Getter;
import lombok.Setter;
import me.fodded.core.commands.CommandManager;
import me.fodded.core.listeners.PlayerConnectListener;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.managers.ranks.RankManager;
import me.fodded.core.managers.stats.Database;
import me.fodded.core.managers.stats.Redis;
import me.fodded.core.managers.stats.loaders.DatabaseLoader;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

public class Core {

    @Getter
    public static Core instance;

    @Getter
    private Server server;

    @Getter
    private JavaPlugin plugin;

    @Getter
    private static final String noPermissionMessage = "&cYou do not have permissions to run that command.";

    public Core(Server server, JavaPlugin plugin) {
        instance = this;

        this.server = server;
        this.plugin = plugin;
    }

    public Core() {
        instance = this;

    }

    public void initializeCore() {
        ConfigLoader.getInstance().createConfig("core-config.yml");

        CommandManager.getInstance().initializeCommands();
        RankManager.getInstance().initializeRanks();

        String host = ConfigLoader.getInstance().getConfig("core-config.yml").getString("redis-ip");
        int port = ConfigLoader.getInstance().getConfig("core-config.yml").getInt("redis-port");

        new Redis(host, port);
        new Database(ConfigLoader.getInstance().getConfig("core-config.yml").getString("mongodb-connection"));

        server.getPluginManager().registerEvents(new PlayerConnectListener(), plugin);
    }

    public void initializeServer() {

    }
}