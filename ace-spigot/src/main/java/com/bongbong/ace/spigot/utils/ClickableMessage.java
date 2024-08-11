package com.bongbong.ace.spigot.utils;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClickableMessage {
    private final List<TextComponent> components = new ArrayList<>();
    private TextComponent current;

    public ClickableMessage(String msg) {
        add(msg);
    }

    public void add(String msg) {
        TextComponent component = new TextComponent(msg);
        this.components.add(component);
        this.current = component;
    }

    private void hover(TextComponent component, String msg) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(msg).create()));
    }

    public ClickableMessage hover(String msg) {
        hover(current, msg);
        return this;
    }

    public ClickableMessage hoverAll(String msg) {
        components.forEach(component -> hover(component, msg));
        return this;
    }

    private void command(TextComponent component, String command) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    private void link(TextComponent component, String url) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    public ClickableMessage command(String command) {
        command(this.current, command);
        return this;
    }

    public ClickableMessage commandAll(String command) {
        components.forEach(component -> this.command(component, command));
        return this;
    }

    public ClickableMessage color(String color) {
        current.setColor(net.md_5.bungee.api.ChatColor.getByChar(color.charAt(1)));
        return this;
    }

    public ClickableMessage color(ChatColor color) {
        current.setColor(color.asBungee());
        return this;
    }

    public ClickableMessage style(ChatColor color) {
        switch (color) {
            case UNDERLINE:
                current.setUnderlined(true);
                break;
            case BOLD:
                current.setBold(true);
                break;
            case ITALIC:
                current.setItalic(true);
                break;
            case MAGIC:
                current.setObfuscated(true);
                break;
        }
        return this;
    }

    public void sendToPlayer(Player player) {
        player.spigot().sendMessage(components.toArray(new BaseComponent[0]));
    }
}
