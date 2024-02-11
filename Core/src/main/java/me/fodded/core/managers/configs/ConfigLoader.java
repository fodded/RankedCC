package me.fodded.core.managers.configs;

import lombok.Getter;
import me.fodded.core.Core;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    private static ConfigLoader instance;
    public ConfigLoader() {
        instance = this;
    }

    public void createConfig(String fileName) {
        try {
            if(new File(Core.getInstance().getPlugin().getDataFolder(), fileName).exists()) {
                return;
            }

            FileUtils.copyInputStreamToFile(
                    Core.getInstance().getPlugin().getResource(fileName),
                    new File("plugins/" + Core.getInstance().getPlugin().getName() + "/" + fileName)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig(String fileName) {
        File file = new File(Core.getInstance().getPlugin().getDataFolder(), fileName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getConfig(String fileName) {
        File file = new File(Core.getInstance().getPlugin().getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static ConfigLoader getInstance() {
        if(instance == null) {
            return new ConfigLoader();
        }

        return instance;
    }
 }
