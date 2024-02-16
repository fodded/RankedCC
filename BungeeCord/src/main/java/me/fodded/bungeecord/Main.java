package me.fodded.bungeecord;

import lombok.Getter;
import me.fodded.proxycore.ProxyCore;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        ProxyCore.initialize(instance);
    }

    @Override
    public void onDisable() {

    }
}