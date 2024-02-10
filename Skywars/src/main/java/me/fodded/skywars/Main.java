package me.fodded.skywars;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.utils.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        Core core = new Core(getServer(), instance);
        core.initializeCore();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(
                StringUtils.format("&7" + event.getPlayer().getName() + " joined the server")
        );
    }
}