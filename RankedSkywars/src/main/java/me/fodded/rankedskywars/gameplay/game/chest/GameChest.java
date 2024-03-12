package me.fodded.rankedskywars.gameplay.game.chest;

import lombok.Getter;
import me.fodded.spigotcore.gameplay.games.map.GameMapConfig;
import me.fodded.spigotcore.utils.ServerLocations;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter
public class GameChest {

    private final GameChestType gameChestType;
    private final Location location;

    public GameChest(Location location, GameChestType gameChestType) {
        this.location = location;
        this.gameChestType = gameChestType;
    }

    // chests:
    //     island1:
    //       - loc1
    //       - loc2

    public static Map<GameChestType, Set<Location>> getGameChestMap(World world, String configPath) {
        Map<GameChestType, Set<Location>> gameChestMap = new HashMap<>();
        FileConfiguration config = GameMapConfig.getInstance().getGameInfo();

        // we get chests.all_types here
        for(String chestTypePath : config.getConfigurationSection(configPath).getKeys(false)) {
            List<String> chestTypeLocations = config.getStringList(configPath + "." + chestTypePath);
            Set<Location> gameChestLocationsSet = new HashSet<>();

            for(String serializedChestLocation : chestTypeLocations) {
                Location location = ServerLocations.deserializeLocation(world, serializedChestLocation);
                gameChestLocationsSet.add(location);
            }

            GameChestType gameChestType = GameChestType.valueOf(chestTypePath);
            gameChestMap.put(gameChestType, gameChestLocationsSet);
        }
        return gameChestMap;
    }
}