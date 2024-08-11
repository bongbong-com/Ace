package com.bongbong.ace.velocity.network;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.CommandManager;
import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.velocity.utils.Colors;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class LimboCommandIssuer implements CommandIssuer {
    private final VelocityCommandManager manager;
    private final Player player;

    LimboCommandIssuer(VelocityCommandManager manager, Player player) {
        this.manager = manager;
        this.player = player;
    }

    @Override
    public CommandSource getIssuer() {
        return player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public CommandManager getManager() {
        return manager;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public void sendMessageInternal(String message) {
        player.sendMessage(Colors.get(message));
    }

    @Override
    public boolean hasPermission(String name) {
        return player.hasPermission(name);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimboCommandIssuer that = (LimboCommandIssuer) o;
        return Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}