package me.fodded.core.commands;

import lombok.Getter;
import me.fodded.core.commands.impl.GameModeCommand;
import me.fodded.core.commands.impl.RankCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private static CommandManager instance;

    @Getter
    private List<PluginCommand> commandsList = new ArrayList<>();

    public CommandManager() {
        instance = this;
    }

    public void initializeCommands() {
        commandsList.add(new GameModeCommand());
        commandsList.add(new RankCommand());
    }

    public static CommandManager getInstance() {
        if(instance == null) {
            return new CommandManager();
        }

        return instance;
    }
}
