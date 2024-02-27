package me.fodded.spigotcore.utils;

import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StringUtils {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    // Getting current date is a heavy resource taking operation
    // So, we basically "cache" it every 60 seconds to not get it every time
    private static String date;
    private static long lastTime = 0;
    public static String getDate() {
        if(lastTime > System.currentTimeMillis()) {
            return date;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        lastTime = System.currentTimeMillis() + 1000 * 60;
        date = dateFormat.format(System.currentTimeMillis());
        return date;
    }

    private static NumberFormat numberFormat;
    public static NumberFormat getNumberFormat() {
        if(numberFormat == null) {
            numberFormat = NumberFormat.getInstance(new Locale("en", "US"));
        }
        return numberFormat;
    }
}
