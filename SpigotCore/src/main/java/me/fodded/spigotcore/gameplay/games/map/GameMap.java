package me.fodded.spigotcore.gameplay.games.map;

import lombok.Getter;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

@Getter
public class GameMap {

    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private String worldName;
    private World world;

    public GameMap(String worldName) {
        File gameMapsFolder = new File(
                SpigotCore.getInstance().getPlugin().getDataFolder(),
                "gameMaps"
        );

        this.sourceWorldFolder = new File(
                gameMapsFolder,
                worldName
        );

        load();
    }

    public void load() {
        if(isLoaded()) return;

        this.worldName = sourceWorldFolder.getName() + "_active_" + System.currentTimeMillis();
        this.activeWorldFolder = new File(
                Bukkit.getWorldContainer().getParentFile(),
                worldName
        );

        try {
            FileUtils.copy(sourceWorldFolder, activeWorldFolder);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load " + sourceWorldFolder);
            return;
        }

        this.world = Bukkit.createWorld(
                new WorldCreator(activeWorldFolder.getName())
        );

        if(world != null) this.world.setAutoSave(false);
    }

    public void unload() {
        if(world != null) Bukkit.unloadWorld(world, false);
        if(activeWorldFolder != null) FileUtils.delete(activeWorldFolder);

        world = null;
        activeWorldFolder = null;
    }

    public boolean isLoaded() {
        return getWorld() != null;
    }
}
