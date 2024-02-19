package me.fodded.skywars.managers;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.impl.skywars.ranked.RankedStats;
import me.fodded.core.managers.stats.impl.skywars.ranked.RankedStatsDataManager;
import me.fodded.spigotcore.configs.ConfigLoader;
import me.fodded.spigotcore.gameplay.scoreboard.AbstractScoreboard;
import me.fodded.spigotcore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SkywarsLobbyScoreboard extends AbstractScoreboard {

    public SkywarsLobbyScoreboard(UUID uniqueId) {
        super(uniqueId);
    }

    protected void updateScoreboard() {
        Scoreboard scoreboard = getScoreboard();
        Objective o = scoreboard.getObjective("scoreboard");
        if(o == null) {
            o = scoreboard.registerNewObjective("scoreboard", "dummy");
            o.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(getUniqueId());
        o.setDisplayName(StringUtils.format(
                ConfigLoader.getInstance()
                        .getConfig("swlobby-"+generalStats.getChosenLanguage()+"-lang.yml")
                        .getStringList("scoreboard").get(0)
        ));

        int index = 0;
        List<String> scores = getScoreboardStrings();
        for(String string : scores) {
            String entry = ChatColor.values()[index].toString();
            Team team = scoreboard.getTeam(index+"");

            if(team == null) {
                team = scoreboard.registerNewTeam(index+"");
                team.addEntry(entry);

                o.getScore(entry).setScore(index);
            }

            team.setPrefix(string);
            index++;
        }
    }

    protected void updateTab() {
        Player player = Bukkit.getPlayer(getUniqueId());
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            if(!player.canSee(eachPlayer)) {
                continue;
            }

            setPrefix(eachPlayer);
        }
    }

    protected void setPrefix(Player eachPlayer) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(eachPlayer.getUniqueId());
        String prefix = generalStats.getPrefix().isEmpty() ? generalStats.getRank().getPrefix() : generalStats.getPrefix() + " ";

        String teamName = "0" + (Rank.values().length - generalStats.getRank().getPriority()) + generalStats.getRank();

        Scoreboard scoreboard = getScoreboard();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setPrefix(StringUtils.format(prefix));
        team.addEntry(eachPlayer.getName());
    }

    protected List<String> getScoreboardStrings() {
        UUID uniqueId = getUniqueId();
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(uniqueId);

        List<String> completedList = new LinkedList<>();
        List<String> scoreboardStrings = ConfigLoader.getInstance()
                .getConfig("swlobby-"+generalStats.getChosenLanguage()+"-lang.yml")
                .getStringList("scoreboard");

        RankedStats rankedStats = RankedStatsDataManager.getInstance().getCachedValue(uniqueId);
        for(int i = 1; i < scoreboardStrings.size(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("en", "US"));

            completedList.add(StringUtils.format(scoreboardStrings.get(i)
                    .replace("{time}", dateFormat.format(System.currentTimeMillis()) + "")
                    .replace("{rating}", numberFormat.format(rankedStats.getRankedSeasonStatsList().get(0).getRating()))
                    .replace("{kills}", numberFormat.format(rankedStats.getKills()))
            ));
        }

        Collections.reverse(completedList);
        return completedList;
    }
}
