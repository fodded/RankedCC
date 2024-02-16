package me.fodded.spigotcore.configs;

import me.fodded.spigotcore.SpigotCore;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {

    private static ConfigLoader instance;
    private JavaPlugin plugin;

    public ConfigLoader() {
        instance = this;

        System.out.println(SpigotCore.getInstance() + " Spigotcore");
        System.out.println(SpigotCore.getInstance().getPlugin() + " plugin");

        plugin = SpigotCore.getInstance().getPlugin();
    }

    public void createConfig(String fileName) {
        try {
            if(new File(plugin.getDataFolder(), fileName).exists()) {
                return;
            }

            FileUtils.copyInputStreamToFile(
                    plugin.getResource(fileName),
                    new File("plugins/" + plugin.getName() + "/" + fileName)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static ConfigLoader getInstance() {
        if(instance == null) {
            return new ConfigLoader();
        }

        return instance;
    }
 }
