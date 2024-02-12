package me.fodded.skywars;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.core.managers.chat.ChatManager;
import me.fodded.core.managers.configs.ConfigLoader;
import me.fodded.core.managers.stats.Redis;
import me.fodded.skywars.listeners.PlayerConnectListener;
import me.fodded.skywars.listeners.jedis.JedisListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ForkJoinPool;

public class Main extends JavaPlugin {

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
            Redis.getInstance().getJedisPool().getResource().subscribe(jedisListener, new String[]{"uploadData", "loadData", "editData"});
        });

        ConfigLoader.getInstance().createConfig("skywars-config.yml");
        getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);

        new ChatManager(3000L);
    }

    @Override
    public void onDisable() {
        Redis.getInstance().getJedisPool().close();
    }
}