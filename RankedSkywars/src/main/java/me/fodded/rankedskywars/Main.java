package me.fodded.rankedskywars;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.rankedskywars.gameplay.commands.CreateMapCommand;
import me.fodded.rankedskywars.gameplay.listeners.PlayerActionListener;
import me.fodded.rankedskywars.gameplay.listeners.PlayerConnectListener;
import me.fodded.rankedskywars.gameplay.listeners.WorldListener;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.chat.ChatManager;
import me.fodded.spigotcore.gameplay.commands.CommandManager;
import me.fodded.spigotcore.tasks.KeepDayTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RMap;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        SpigotCore spigotCore = SpigotCore.getInstance();
        SpigotCore.initialize(instance);
        spigotCore.initializeConfigs();
        spigotCore.initializeListeners();
        registerCommands();

        FileConfiguration config = ConfigLoader.getInstance().getConfig("core-config.yml");
        spigotCore.initializeRedis(config);
        spigotCore.initializeDatabase(config);
        initializeConfigs();

        getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        new ChatManager(1000L);

        // server manager
        SpigotCore.getInstance().initializeServerManager();

        KeepDayTask keepDayTask = new KeepDayTask();
        keepDayTask.runTaskTimer(this, 0, 100);
    }

    private void initializeConfigs() {
        ConfigLoader configLoader = ConfigLoader.getInstance();
        configLoader.createConfig("mainlobby.yml");
        configLoader.createConfig("english-lang.yml");
        configLoader.createConfig("russian-lang.yml");
    }

    private void registerCommands() {
        CommandManager commandManager = CommandManager.getInstance();
        commandManager.initializeCommands();
        commandManager.addCommand(new CreateMapCommand());
        commandManager.removeCommand("nick");
        commandManager.removeCommand("rank");
    }

    @Override
    public void onDisable() {
        RMap<Integer, Integer> playersMap = Core.getInstance().getRedis().getRedissonClient().getMap("playersMap");
        playersMap.remove(Integer.parseInt(SpigotCore.getInstance().getServerName().substring("Ranked-Skywars-Game".length()+1)));
    }
}