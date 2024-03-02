package me.fodded.mainlobby;

import lombok.Getter;
import me.fodded.core.Core;
import me.fodded.mainlobby.gameplay.commands.SetLobbyCommand;
import me.fodded.mainlobby.listeners.PlayerActionListener;
import me.fodded.mainlobby.listeners.PlayerConnectListener;
import me.fodded.mainlobby.listeners.WorldListener;
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

        ConfigLoader.getInstance().createConfig("mainlobby.yml");
        ConfigLoader.getInstance().createConfig("english-lang.yml");
        ConfigLoader.getInstance().createConfig("russian-lang.yml");

        getServer().getPluginManager().registerEvents(new PlayerConnectListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        new ServerLocations("mainlobby.yml");
        new ChatManager(1000L);

        // server manager
        SpigotCore.getInstance().initializeServerManager();

        KeepDayTask keepDayTask = new KeepDayTask();
        keepDayTask.runTaskTimer(this, 0, 100);
    }

    @Override
    public void onDisable() {
        RMap<Integer, Integer> playersMap = Core.getInstance().getRedis().getRedissonClient().getMap("playersMap");
        playersMap.remove(Integer.parseInt(SpigotCore.getInstance().getServerName().substring("Main-Lobby".length()+1)));
    }
}