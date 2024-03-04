package me.fodded.bungeecord.utils;

import me.fodded.bungeecord.managers.LanguageManager;
import me.fodded.core.managers.stats.impl.profile.GeneralStats;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class StringUtils {
    public static BaseComponent[] format(String text) {
        return TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', text));
    }

    public static String formatString(String text) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void sendMessage(ProxiedPlayer player, String configMessageKey) {
        BaseComponent[] message = format(LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getString(configMessageKey));
        player.sendMessage(message);
    }

    public static void sendMessage(ProxiedPlayer player, List<String> messageList) {
        for(String message : messageList) {
            player.sendMessage(StringUtils.format(message));
        }
    }

    public static String getMessage(ProxiedPlayer player, String configMessageKey) {
        return LanguageManager.getInstance().getLanguageConfig(player.getUniqueId()).getString(configMessageKey);
    }

    public static TextComponent getTextComponent(String displayText, String displayHoverText, String onClickCommand) {
        TextComponent textComponent = new TextComponent(StringUtils.formatString(displayText));
        if(displayHoverText != null) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(displayHoverText).create()));
        }

        if(onClickCommand != null) {
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, onClickCommand));
        }

        return textComponent;
    }

    public static String getReplacedPlaceholders(String text, String playerPrefix) {
        return StringUtils.formatString(text.replace("%player_sent_request%", playerPrefix)
                .replace("%target_sent_request%", playerPrefix));
    }

    public static String getPlayerPrefix(ProxiedPlayer player) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRemoteValue(player.getUniqueId());
        String prefix = generalStats.getRank().getPrefix() + player.getName();
        if(!generalStats.getPrefix().isEmpty()) {
            prefix = generalStats.getPrefix() + " " + player.getName();
        }
        return prefix;
    }

    public static String getPlayerPrefix(UUID uniqueId) {
        GeneralStats generalStats = GeneralStatsDataManager.getInstance().getRemoteValue(uniqueId);
        String prefix = generalStats.getRank().getPrefix() + generalStats.getLastName();
        if(!generalStats.getPrefix().isEmpty()) {
            prefix = generalStats.getPrefix() + " " + generalStats.getLastName();
        }
        return prefix;
    }
}
