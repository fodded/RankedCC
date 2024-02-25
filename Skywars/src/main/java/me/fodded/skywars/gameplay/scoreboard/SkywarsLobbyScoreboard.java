package me.fodded.skywars.gameplay.scoreboard;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import me.fodded.core.managers.stats.impl.skywars.ranked.RankedStats;
import me.fodded.core.managers.stats.impl.skywars.ranked.RankedStatsDataManager;
import me.fodded.spigotcore.gameplay.scoreboard.AbstractScoreboard;
import me.fodded.spigotcore.languages.LanguageManager;
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
import java.util.concurrent.TimeUnit;

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

        o.setDisplayName(StringUtils.format(
                LanguageManager.getInstance().getLanguageConfig(getUniqueId())
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

            if(string.length() <= 16) {
                team.setPrefix(StringUtils.format(string));
                team.setSuffix(StringUtils.format("&f"));
            } else {
                String firstLine = string.substring(0, 16);
                String rightSideLine = firstLine.split(" ")[firstLine.split(" ").length-1];

                String colorCode;
                if(rightSideLine.split("&").length>2) {
                    colorCode = rightSideLine.substring(0,4);
                } else {
                    colorCode = "&" + rightSideLine.charAt(1);
                }

                team.setPrefix(StringUtils.format(string.substring(0, 16)));
                team.setSuffix(StringUtils.format(colorCode + string.substring(16)));
            }
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
        List<String> scoreboardStrings = LanguageManager.getInstance().getLanguageConfig(uniqueId)
                .getStringList("scoreboard");

        RankedStats rankedStats = RankedStatsDataManager.getInstance().getCachedValue(uniqueId);
        for(int i = 1; i < scoreboardStrings.size(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("en", "US"));

            completedList.add(
                    scoreboardStrings.get(i)
                            .replace("{time}", dateFormat.format(System.currentTimeMillis()))
                            .replace("{time_played}", TimeUnit.MILLISECONDS.toHours(generalStats.getTimePlayed())+"")
                            .replace("{friends}", numberFormat.format(generalStats.getFriendList().size()))
                            .replace("{rating}", numberFormat.format(rankedStats.getRankedSeasonStatsList().get(0).getRating()))
                            .replace("{losses}", numberFormat.format(rankedStats.getLosses()))
                            .replace("{deaths}", numberFormat.format(rankedStats.getDeaths()))
                            .replace("{rank}", generalStats.getRank().name())
                            .replace("{rank_prefix}", generalStats.getRank().getPrefix())
                            .replace("{kills}", numberFormat.format(rankedStats.getKills()))
            );
        }

        Collections.reverse(completedList);
        return completedList;
    }
}
