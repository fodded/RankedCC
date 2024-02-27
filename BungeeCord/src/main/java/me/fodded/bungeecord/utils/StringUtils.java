package me.fodded.bungeecord.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class StringUtils {
    public static BaseComponent[] format(String text) {
        return TextComponent.fromLegacyText(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', text));
    }
}
