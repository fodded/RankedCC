package me.fodded.spigotcore.gameplay.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class AbstractScoreboard {

    private final UUID uniqueId;
    private final Scoreboard scoreboard;

    @Getter
    private static Map<UUID, AbstractScoreboard> scoreboardMap = new HashMap<>();

    public AbstractScoreboard(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        scoreboardMap.put(uniqueId, this);
        Bukkit.getPlayer(uniqueId).setScoreboard(scoreboard);
    }

    public void update() {
        updateScoreboard();
        updateTab();
    }

    protected abstract void updateScoreboard();
    protected abstract void updateTab();

    protected abstract void setPrefix(Player eachPlayer, boolean hasPermissions);

    protected abstract List<String> getScoreboardStrings();

    public void removeScoreboard() {
        scoreboardMap.remove(uniqueId);
    }

    public static AbstractScoreboard getScoreboardManager(UUID uniqueId) {
        return scoreboardMap.get(uniqueId);
    }
}
