package me.fodded.bungeecord;

import lombok.Getter;
import me.fodded.proxycore.ProxyCore;
import me.fodded.proxycore.configs.ConfigLoader;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

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
    }

    @Override
    public void onDisable() {

    }
}