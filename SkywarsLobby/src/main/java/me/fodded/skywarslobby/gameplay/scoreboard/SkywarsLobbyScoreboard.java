package me.fodded.skywarslobby.gameplay.scoreboard;

import me.fodded.core.managers.ranks.Rank;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
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
        boolean hasPermissions = Rank.hasPermission(Rank.HELPER, player.getUniqueId());
        for(Player eachPlayer : Bukkit.getOnlinePlayers()) {
            if(!player.canSee(eachPlayer)) {
                continue;
            }

            setPrefix(eachPlayer, hasPermissions);
        }
    }

    @Override
    protected void setPrefix(Player eachPlayer, boolean hasPermissions) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(eachPlayer.getUniqueId());
        boolean isPlayerDisguised = !generalStats.getDisguisedName().isEmpty();

        Rank rank = getRank(generalStats, isPlayerDisguised);

        String prefix = getPrefix(rank, generalStats, isPlayerDisguised);
        String suffix = getPlayerSuffix(hasPermissions, isPlayerDisguised, generalStats);
        String teamName = "0" + (Rank.values().length - rank.getPriority()) + rank;

        Scoreboard scoreboard = getScoreboard();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setSuffix(StringUtils.format(" " + suffix));
        team.setPrefix(StringUtils.format(prefix));
        team.addEntry(eachPlayer.getName());
    }

    private String getPrefix(Rank rank, GeneralStats generalStats, boolean isPlayerDisguised) {
        String prefix = rank.getPrefix();
        if(!generalStats.getPrefix().isEmpty() && !isPlayerDisguised) {
            prefix = generalStats.getPrefix();
        }
        return prefix;
    }

    private Rank getRank(GeneralStats generalStats, boolean isPlayerDisguised) {
        Rank rank = generalStats.getRank();
        if(isPlayerDisguised) {
            rank = generalStats.getDisguisedRank();
        }
        return rank;
    }

    private String getPlayerSuffix(boolean hasPermissions, boolean isPlayerDisguised, GeneralStats generalStats) {
        String suffix = "";
        if(hasPermissions && isPlayerDisguised) {
            suffix += "&c&lD";
        }
        if(hasPermissions && generalStats.isVanished()) {
            suffix += "&c&lV";
        }
        if(!hasPermissions || (!generalStats.isVanished() && !isPlayerDisguised)){
            suffix = "&7";
        }
        return suffix;
    }

    protected List<String> getScoreboardStrings() {
        UUID uniqueId = getUniqueId();
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getCachedValue(uniqueId);

        List<String> completedList = new LinkedList<>();
        List<String> scoreboardStrings = LanguageManager.getInstance()
                .getLanguageConfig(uniqueId)
                .getStringList("scoreboard");

        NumberFormat numberFormat = StringUtils.getNumberFormat();
        for(int i = 1; i < scoreboardStrings.size(); i++) {
            StringBuilder sb = new StringBuilder(scoreboardStrings.get(i));
            replaceAll(sb, "{time}", StringUtils.getDate());
            replaceAll(sb, "{time_played}", Long.toString(TimeUnit.MILLISECONDS.toHours(generalStats.getTimePlayed())));
            replaceAll(sb, "{friends}", numberFormat.format(generalStats.getFriendList().size()));
            replaceAll(sb, "{rank}", generalStats.getRank().name());
            replaceAll(sb, "{rank_prefix}", generalStats.getRank().getPrefix());

            completedList.add(sb.toString());
        }

        Collections.reverse(completedList);
        return completedList;
    }

    private void replaceAll(StringBuilder sb, String target, String replacement) {
        int index = sb.indexOf(target);
        while(index != -1) {
            sb.replace(index, index + target.length(), replacement);
            index += replacement.length();
            index = sb.indexOf(target, index);
        }
    }
}

