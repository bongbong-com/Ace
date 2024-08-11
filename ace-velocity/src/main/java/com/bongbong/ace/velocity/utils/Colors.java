package com.bongbong.ace.velocity.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Colors {
    public static Component get(String string) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }

    public static String translate(String string) {
        return string.replace('&', '\u00a7');
    }

    public static Component strip(String string) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }

    public static String convertToString(Component component) {
        return Colors.translate(LegacyComponentSerializer.legacyAmpersand().serialize(component));
    }
}
