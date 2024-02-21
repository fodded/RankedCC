package me.fodded.spigotcore.gameplay.commands;

import lombok.Getter;
import lombok.SneakyThrows;
import me.fodded.core.managers.ranks.Rank;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class PluginCommand implements CommandExecutor {

    private Rank rank;
    private String description, usage, name;
    private List<String> aliases = new ArrayList<>();

    private CommandInfo commandInfo;
    @SneakyThrows
    public PluginCommand() {
        commandInfo = getClass().getAnnotation(CommandInfo.class);
        Objects.requireNonNull(commandInfo, "Command Info annotation was not provided");

        rank = commandInfo.rank();
        name = commandInfo.name();
        usage = commandInfo.usage();
        description = commandInfo.description();

        // registering the command
        SpigotCore.getInstance().getPlugin().getServer().getPluginCommand(name).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        PluginCommand command = getCommand(cmd.getName());
        if(command == null) {
            return false;
        }

        if(sender instanceof Player) {
            if(!sender.isOp()) {
                if (!Rank.hasPermission(command.getRank(), ((Player) sender).getUniqueId())) {
                    sender.sendMessage(StringUtils.format(SpigotCore.getInstance().getNoPermissionMessage()));
                    return false;
                }
            }
        }

        onCommand(sender, args);
        return true;
    }

    private PluginCommand getCommand(String cmd) {
        for(PluginCommand command : CommandManager.getInstance().getCommandsList()) {
            if(command.getName().equalsIgnoreCase(cmd)) {
                return command;
            }
        }
        return null;
    }

    public abstract void onCommand(CommandSender sender, String[] args);
}
