package me.fodded.skywars;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin implements Listener {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        Core core = new Core(getServer(), instance);
        core.initializeCore();

        ConfigLoader.getInstance().createConfig("skywars-config.yml");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(
                StringUtils.format("&c" + event.getPlayer().getName() + " joined the server")
        );
    }
}