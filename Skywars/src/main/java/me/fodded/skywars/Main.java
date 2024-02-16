package me.fodded.skywars;

import lombok.Getter;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.chat.ChatManager;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.tasks.KeepDayTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.K;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        SpigotCore.initialize(instance);

        ConfigLoader.getInstance().createConfig("skywars-config.yml");
        //getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);

        new ChatManager(3000L);

        KeepDayTask keepDayTask = new KeepDayTask();
        keepDayTask.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {

    }
}