package me.fodded.skywarslobby;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.skywarslobby.gameplay.commands.SetLobbyCommand;
import me.fodded.skywarslobby.gameplay.listeners.PlayerActionListener;
import me.fodded.skywarslobby.gameplay.listeners.PlayerConnectListener;
import me.fodded.skywarslobby.gameplay.listeners.WorldListener;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.chat.ChatManager;
import me.fodded.spigotcore.gameplay.commands.CommandManager;
import me.fodded.spigotcore.tasks.KeepDayTask;
import me.fodded.spigotcore.utils.ServerLocations;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RMap;

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
        CommandManager.getInstance().addCommand(new SetLobbyCommand());

        FileConfiguration config = ConfigLoader.getInstance().getConfig("core-config.yml");
        SpigotCore.getInstance().initializeRedis(config);
        SpigotCore.getInstance().initializeDatabase(config);

        ConfigLoader.getInstance().createConfig("swlobby.yml");
        ConfigLoader.getInstance().createConfig("english-lang.yml");
        ConfigLoader.getInstance().createConfig("russian-lang.yml");

        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);

        new ServerLocations("swlobby.yml");
        new ChatManager(1000L);

        // server manager
        SpigotCore.getInstance().initializeServerManager();

        KeepDayTask keepDayTask = new KeepDayTask();
        keepDayTask.runTaskTimer(this, 0, 100);
    }

    @Override
    public void onDisable() {
        RMap<Integer, Integer> playersMap = Core.getInstance().getRedis().getRedissonClient().getMap("playersMap");
        playersMap.remove(Integer.parseInt(SpigotCore.getInstance().getServerName().substring("Skywars-Lobby".length()+1)));
    }
}
