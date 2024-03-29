package me.fodded.spigotcore.gameplay.commands;

import lombok.Getter;
import me.fodded.spigotcore.gameplay.commands.impl.*;

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
        commandsList.add(new ConfigReloadCommand());
        commandsList.add(new NickCommand());
        commandsList.add(new RevealNickCommand());
        commandsList.add(new DisguiseHistoryCommand());
    }

    public void addCommand(PluginCommand command) {
        commandsList.add(command);
    }

    public void removeCommand(String commandName) {
        PluginCommand command = getCommand(commandName);
        commandsList.remove(command);
    }

    private PluginCommand getCommand(String name) {
        for(PluginCommand pluginCommand : commandsList) {
            if(pluginCommand.getName().equalsIgnoreCase(name)) {
                return pluginCommand;
            }
        }
        return null;
    }

    public static CommandManager getInstance() {
        if(instance == null) {
            return new CommandManager();
        }

        return instance;
    }
}
