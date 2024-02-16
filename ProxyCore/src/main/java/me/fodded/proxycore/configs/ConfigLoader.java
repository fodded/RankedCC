package me.fodded.proxycore.configs;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    private static ConfigLoader instance;
    public ConfigLoader() {
        instance = this;
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

    public static ConfigLoader getInstance() {
        if(instance == null) {
            return new ConfigLoader();
        }
        return instance;
    }
}
