package me.fodded.skywars;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.managers.stats.Redis;
import me.fodded.core.utils.StringUtils;
import me.fodded.skywars.listeners.PlayerConnectListener;
import me.fodded.skywars.listeners.jedis.JedisListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ForkJoinPool;

public class Main extends JavaPlugin implements Listener {

    @Getter
    private static Main instance;

    @Getter
    private JedisListener jedisListener;

    @Override
    public void onEnable() {
        instance = this;
        Core core = new Core(getServer(), instance);
        core.initializeCore();

        jedisListener = new JedisListener();
        ForkJoinPool.commonPool().submit(() -> {
            Redis.getInstance().getJedisPool().getResource().subscribe(jedisListener, new String[]{"uploadData", "loadData"});
        });

        ConfigLoader.getInstance().createConfig("skywars-config.yml");
        getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);
    }

    @Override
    public void onDisable() {
        Redis.getInstance().getJedisPool().close();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(
                StringUtils.format("&c" + event.getPlayer().getName() + " joined the server")
        );
    }
}