package me.fodded.skywarslobby.gameplay.commands;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.commands.CommandInfo;
import me.fodded.spigotcore.gameplay.commands.PluginCommand;
import me.fodded.spigotcore.utils.ServerLocations;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandInfo(name = "setlobby", usage = "/setlobby", description = "set new lobby location", rank = Rank.ADMIN)
public class SetLobbyCommand extends PluginCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            return;
        }

        if(args.length == 1) {
            UUID uniqueId = ((Player) sender).getUniqueId();

            GeneralStatsDataManager generalStatsDataManager = GeneralStatsDataManager.getInstance();
            generalStatsDataManager.applyChangeToRedis(
                    uniqueId,
                    generalStats -> generalStats.addFriendToList(UUID.randomUUID())
            );
            return;
        }

        FileConfiguration config = ConfigLoader.getInstance().getConfig("swlobby.yml");
        Location location = ((Player) sender).getLocation();

        config.set("lobby-location.world", location.getWorld().getName());
        config.set("lobby-location.x", location.getBlockX() + 0.5);
        config.set("lobby-location.y", location.getY());
        config.set("lobby-location.z", location.getBlockZ() + 0.5);
        config.set("lobby-location.yaw", location.getYaw());
        config.set("lobby-location.pitch", 0);

        ConfigLoader.getInstance().saveConfig(config, "swlobby.yml");
        ConfigLoader.getInstance().clearConfigCache();

        ServerLocations.getInstance().setLobbyLocation(location.clone());

        sender.sendMessage(StringUtils.format("&aYou successfully changed lobby location"));
    }
}
