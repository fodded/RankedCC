package me.fodded.spigotcore.gameplay.commands.impl;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.languages.LanguageManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "configreload", usage = "/configreload", description = "reloads all configs", rank = Rank.ADMIN)
public class ConfigReloadCommand extends PluginCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ConfigLoader.getInstance().clearConfigCache();
        LanguageManager.getInstance().initializeLanguageList();
        sender.sendMessage(StringUtils.format("&aYou've successfully updated all configs!"));
    }
}
