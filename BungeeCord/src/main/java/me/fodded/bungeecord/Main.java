package me.fodded.bungeecord;

import lombok.Getter;
import lombok.Setter;
import me.fodded.bungeecord.listeners.PlayerConnectListener;
import me.fodded.core.managers.stats.Database;
import me.fodded.core.managers.stats.Redis;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.IOException;

public class Main extends Plugin {

    @Getter
    private static Main instance;

    @Getter @Setter
    private JedisPool jedisPool;

    @Override
    public void onEnable() {
        instance = this;
        createConfig();

        // The core API is not really made for the bungeecord, so the code doesn't look like it belongs here
        Configuration config = getConfig();
        new Redis(
                config.getString("redis-ip"),
                config.getInt("redis-port")
        );

        new Database(getConfig().getString("mongodb-connection"));
        getProxy().getPluginManager().registerListener(this, new PlayerConnectListener());
    }

    @Override
    public void onDisable() {

    }

    public void createConfig() {
        File file = new File(ProxyServer.getInstance().getPluginsFolder() + "/config.yml");
        try {
            if(!file.exists()) {
                file.createNewFile();
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                config.set("mongodb-connection", "connection-url");
                config.set("redis-port", 6379);
                config.set("redis-ip", "localhost");
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig() {
        File file = new File(ProxyServer.getInstance().getPluginsFolder() + "/config.yml");
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}