package me.fodded.bungeecord.utils;

import me.fodded.bungeecord.managers.LanguageManager;
import me.fodded.bungeecord.managers.friends.FriendManager;
import me.fodded.core.managers.ranks.Rank;
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

    public static void sendPrivateMessage(String message, ProxiedPlayer player, ProxiedPlayer targetPlayer, GeneralStats targetPlayerGeneralStats) {
        if(targetPlayerGeneralStats.getIgnoreList().contains(player.getUniqueId()) && !Rank.hasPermission(Rank.HELPER, player.getUniqueId())) {
            StringUtils.sendMessage(player, "friends.cant-send-message");
            return;
        }

        if(targetPlayerGeneralStats.isVanished()) {
            StringUtils.sendMessage(player, "friends.no-player-present");
            return;
        }

        boolean isPlayerBypassingSettings = Rank.hasPermission(Rank.YOUTUBE, player.getUniqueId());
        if(isPlayerBypassingSettings) {
            StringUtils.sendPrivateMessage(player, targetPlayer, message);
            return;
        }

        FriendManager friendManager = FriendManager.getInstance();

        boolean isInFriendList = targetPlayerGeneralStats.getFriendList().contains(player.getUniqueId());
        boolean isThePlayerWhoLastSentMessage = friendManager.isThePlayerLastWhoSentMessage(player.getUniqueId(), targetPlayer.getUniqueId());

        if(isInFriendList) {
            StringUtils.sendPrivateMessage(player, targetPlayer, message);
            return;
        }

        if(targetPlayerGeneralStats.isPrivateMessagesEnabled()) {
            StringUtils.sendPrivateMessage(player, targetPlayer, message);
            return;
        }

        if(isThePlayerWhoLastSentMessage) {
            StringUtils.sendPrivateMessage(player, targetPlayer, message);
            return;
        }

        StringUtils.sendMessage(player, "friends.cant-send-message");
    }

    public static void sendPrivateMessage(ProxiedPlayer player, ProxiedPlayer targetPlayer, String message) {
        String senderPrefix = StringUtils.getPlayerPrefix(player);
        String receiverPrefix = StringUtils.getPlayerPrefix(targetPlayer);

        targetPlayer.sendMessage(StringUtils.formatString("&6&lFROM " + senderPrefix + "&f: ") + message);
        player.sendMessage(StringUtils.formatString("&6&lTO " + receiverPrefix + "&f: ") + message);

        FriendManager.getInstance().setLastReceivedMessageFrom(targetPlayer.getUniqueId(), player.getUniqueId());
    }

    public static String getMessageFromArray(String[] args, int firstIndex) {
        String text = "";
        for(int i = firstIndex; i < args.length; i++) {
            text += args[i];
        }
        return text;
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

    public static String getFormattedTime(long unformattedTime) {
        long seconds = unformattedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + "d " + hours % 24 + "h " + minutes % 60 + "m " + seconds % 60 + "s";
    }
}
