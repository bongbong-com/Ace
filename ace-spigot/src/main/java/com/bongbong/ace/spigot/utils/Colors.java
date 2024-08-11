package com.bongbong.ace.spigot.utils;

import org.bukkit.ChatColor;

public class Colors {
    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(translate(s));
    }
}
