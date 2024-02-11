package me.fodded.core;

import lombok.Getter;
import lombok.Setter;
import me.fodded.core.commands.CommandManager;
import me.fodded.core.listeners.PlayerConnectListener;
import me.fodded.core.managers.RedisManager;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.managers.ranks.RankManager;
import me.fodded.core.managers.stats.loaders.StatisticsDatabaseLoader;
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

    @Getter @Setter
    private JedisPool jedisPool;

    @Getter
    private static final String noPermissionMessage = "&cYou do not have permissions to run that command.";

    public Core(Server server, JavaPlugin plugin) {
        instance = this;

        this.server = server;
        this.plugin = plugin;
    }

    public void initializeCore() {
        ConfigLoader.getInstance().createConfig("core-config.yml");

        CommandManager.getInstance().initializeCommands();
        RankManager.getInstance().initializeRanks();
        RedisManager.getInstance().initializeJedis();

        StatisticsDatabaseLoader.getInstance().connectMongoDatabase();

        server.getPluginManager().registerEvents(new PlayerConnectListener(), plugin);
        System.out.println(new ConfigLoader().getConfig("core-config.yml").get("server") + " is the server's name from the config");
    }

    public void initializeServer() {

    }
}