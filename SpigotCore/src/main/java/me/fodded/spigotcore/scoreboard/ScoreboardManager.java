package me.fodded.spigotcore.scoreboard;

import lombok.Getter;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ScoreboardManager {

    @Getter
    private static Map<UUID, ScoreboardManager> scoreboardMap = new HashMap<>();

    private final UUID uniqueId;
    private final Scoreboard scoreboard;

    public ScoreboardManager(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Bukkit.getPlayer(uniqueId).setScoreboard(scoreboard);
        scoreboardMap.put(uniqueId, this);
    }

    public void update() {
        updateScoreboard();
        updateTab();
    }

    private void updateScoreboard() {

    }

    private void updateTab() {
        Player player = Bukkit.getPlayer(uniqueId);
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            if(!player.canSee(eachPlayer)) {
                continue;
            }

            setPrefix(eachPlayer);
            sortTabEntries();
        }
    }

    private void setPrefix(Player eachPlayer) {
        Team team = scoreboard.getTeam(eachPlayer.getUniqueId().toString().replace("-", "").substring(0, 16));
        if (team == null) {
            team = scoreboard.registerNewTeam(eachPlayer.getUniqueId().toString().replace("-", "").substring(0, 16));
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(eachPlayer.getUniqueId());
        String prefix = generalStats.getPrefix().isEmpty() ? generalStats.getRank().getPrefix() + " " : generalStats.getPrefix();

        team.setPrefix(StringUtils.format(prefix));
        team.addEntry(eachPlayer.getName());
    }

    private void sortTabEntries() {
        List<String> entriesList = new ArrayList<>(scoreboard.getEntries());
        Collections.sort(entriesList);
    }

    public void removeScoreboard() {
        scoreboardMap.remove(uniqueId);
    }

    public static ScoreboardManager getScoreboardManager(Player player) {
        if(!scoreboardMap.containsKey(player.getUniqueId())) {
            return new ScoreboardManager(player.getUniqueId());
        }

        return scoreboardMap.get(player.getUniqueId());
    }
}
