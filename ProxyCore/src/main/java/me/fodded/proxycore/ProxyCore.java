package me.fodded.proxycore;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.proxycore.listeners.PlayerConnectListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.redisson.config.Config;

@Data
public class ProxyCore {

    @Getter
    private static ProxyCore instance;
    private Plugin plugin;

    public ProxyCore(Plugin plugin) {
        instance = this;
        this.plugin = plugin;

        Core.initialize();
    }

    public void initializeListeners() {
        plugin.getProxy().getPluginManager().registerListener(plugin, new PlayerConnectListener());
    }

    public void initializeRedis(Configuration config) {
        String redisIp = config.getString("redis-ip");
        int redisPort = config.getInt("redis-port");

        Config redisConfig = new Config();
        redisConfig.useSingleServer().setAddress("redis://" + redisIp + ":" + redisPort);
        Core.getInstance().initializeRedis(redisConfig);
    }

    public void initializeDatabase(Configuration config) {
        String mongodbConnection = config.getString("mongodb-connection");
        Core.getInstance().initializeDatabase(mongodbConnection);
    }

    public static void initialize(Plugin plugin) {
        Preconditions.checkState(instance == null, "The core is already initialized");
        new ProxyCore(plugin);
    }
}