package me.fodded.spigotcore.gameplay.games.state;

import lombok.Getter;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.games.GameInstance;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class GameState implements Listener, IGameState {

    private final GameInstance gameInstance;

    private final Set<Listener> listeners = new HashSet<>();
    private final Set<BukkitTask> tasks = new HashSet<>();

    public GameState(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public abstract int getStateDuration();

    public void startGameState() {
        registerListener(this);
        this.start();
    }

    public void endGameState() {
        listeners.forEach(HandlerList::unregisterAll);
        tasks.forEach(BukkitTask::cancel);
        listeners.clear();
        tasks.clear();
        this.end();
    }

    private void registerListener(Listener listener) {
        JavaPlugin plugin = SpigotCore.getInstance().getPlugin();
        listeners.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    protected void schedule(Runnable runnable, long delay) {
        JavaPlugin plugin = SpigotCore.getInstance().getPlugin();
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        tasks.add(task);
    }

    protected void scheduleRepeating(Runnable runnable, long delay, long interval) {
        JavaPlugin plugin = SpigotCore.getInstance().getPlugin();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, interval);
        tasks.add(task);
    }
}
