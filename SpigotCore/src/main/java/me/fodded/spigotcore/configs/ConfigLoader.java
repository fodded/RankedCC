package me.fodded.spigotcore.configs;

import me.fodded.spigotcore.SpigotCore;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {

    private JavaPlugin plugin;
    private static ConfigLoader instance;

    private final Map<String, FileConfiguration> configCacheMap = new HashMap<>();

    public ConfigLoader() {
        instance = this;
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

    public void saveConfig(FileConfiguration config, String name) {
        try {
            File configFile = new File(SpigotCore.getInstance().getPlugin().getDataFolder(), name);
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if(!configCacheMap.containsKey(fileName)) {
            configCacheMap.put(fileName, YamlConfiguration.loadConfiguration(file));
        }
        return configCacheMap.get(fileName);
    }

    public void clearConfigCache() {
        configCacheMap.clear();
    }

    public static ConfigLoader getInstance() {
        if(instance == null) {
            return new ConfigLoader();
        }

        return instance;
    }
 }
