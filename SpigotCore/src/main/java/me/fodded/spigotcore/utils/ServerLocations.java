package me.fodded.spigotcore.utils;

import lombok.Getter;
import lombok.Setter;
import me.fodded.spigotcore.configs.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

@Setter
public class ServerLocations {

    @Getter
    private Location lobbyLocation;

    @Getter
    private static ServerLocations instance;

    public ServerLocations(String configName) {
        instance = this;
        initializeLobbyLocation(configName);
    }

    private void initializeLobbyLocation(String configName) {
        FileConfiguration config = ConfigLoader.getInstance().getConfig(configName);
        lobbyLocation = new Location(
                Bukkit.getWorld(config.getString("lobby-location.world")),
                config.getDouble("lobby-location.x"),
                config.getDouble("lobby-location.y"),
                config.getDouble("lobby-location.z"),
                Float.parseFloat(config.getString("lobby-location.yaw")),
                Float.parseFloat(config.getString("lobby-location.pitch"))
        ).clone();
    }
}

