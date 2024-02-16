package me.fodded.spigotcore.commands;

import lombok.Getter;
import me.fodded.spigotcore.commands.impl.GameModeCommand;
import me.fodded.spigotcore.commands.impl.RankCommand;
import me.fodded.spigotcore.commands.impl.StatsCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private static CommandManager instance;

    @Getter
    private final List<PluginCommand> commandsList = new ArrayList<>();

    public CommandManager() {
        instance = this;
    }

    public void initializeCommands() {
        commandsList.add(new GameModeCommand());
        commandsList.add(new RankCommand());
        commandsList.add(new StatsCommand());
    }

    public static CommandManager getInstance() {
        if(instance == null) {
            return new CommandManager();
        }

        return instance;
    }
}