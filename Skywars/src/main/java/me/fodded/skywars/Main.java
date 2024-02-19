package me.fodded.skywars;

import lombok.Getter;
import me.fodded.skywars.listeners.PlayerConnectListener;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.chat.ChatManager;
import me.fodded.spigotcore.gameplay.commands.CommandManager;
import me.fodded.spigotcore.tasks.KeepDayTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        SpigotCore.initialize(instance);
        SpigotCore.getInstance().initializeConfigs();
        SpigotCore.getInstance().initializeListeners();

        CommandManager.getInstance().initializeCommands();

        FileConfiguration config = ConfigLoader.getInstance().getConfig("core-config.yml");
        SpigotCore.getInstance().initializeRedis(config);
        SpigotCore.getInstance().initializeDatabase(config);

        ConfigLoader.getInstance().createConfig("swlobby-en-lang.yml");
        ConfigLoader.getInstance().createConfig("swlobby-ru-lang.yml");
        getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);

        new ChatManager(1000L);

        KeepDayTask keepDayTask = new KeepDayTask();
        keepDayTask.runTaskTimer(this, 0, 20);
    }


    @Override
    public void onDisable() {

    }
}