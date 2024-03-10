package me.fodded.spigotcore.gameplay.games.map;

import lombok.Getter;
import me.fodded.spigotcore.SpigotCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class GameMapConfig {

    private final FileConfiguration gameInfo;
    private final File gameInfoFile;

    private static GameMapConfig instance;

    private GameMapConfig() {
        instance = this;
        this.gameInfoFile = new File(SpigotCore.getInstance().getPlugin().getDataFolder(), "gameInfo.yml");
        if(!this.gameInfoFile.exists()) {
            try {
                this.gameInfoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.gameInfo = YamlConfiguration.loadConfiguration(this.gameInfoFile);
    }

    public void saveGameInfo() {
        try {
            this.gameInfo.save(this.gameInfoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameMapConfig getInstance() {
        if(instance == null) {
            return new GameMapConfig();
        }
        return instance;
    }
}