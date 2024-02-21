package me.fodded.skywars.managers;

import lombok.Getter;
import lombok.Setter;
import me.fodded.spigotcore.configs.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ServerLocations {

    @Getter @Setter
    private Location lobbyLocation;

    @Getter
    private static ServerLocations instance;

    public ServerLocations() {
        instance = this;
        initializeLobbyLocation();
    }

    private void initializeLobbyLocation() {
        FileConfiguration config = ConfigLoader.getInstance().getConfig("swlobby.yml");
        lobbyLocation = new Location(Bukkit.getWorld(config.getString("lobby-location.world")),
                config.getDouble("lobby-location.x"),
                config.getDouble("lobby-location.y"),
                config.getDouble("lobby-location.z"),
                Float.parseFloat(config.getString("lobby-location.yaw")),
                Float.parseFloat(config.getString("lobby-location.pitch"))
        ).clone();
    }
}
