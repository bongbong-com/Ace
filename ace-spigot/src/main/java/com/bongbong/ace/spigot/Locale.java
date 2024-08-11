package com.bongbong.ace.spigot;

import com.bongbong.ace.spigot.utils.Colors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Locale {
    NO_PERMISSION("&cno permission"),
    TARGET_NOT_FOUND("&cThe target player has never logged into the server."),
    ONLY_PLAYERS("&cOnly players can use this command!"),
    TARGET_NOT_ONLINE("&cThe target player is not currently online.");

    private final String message;

    public String format() {
        return Colors.translate(message);
    }
}

