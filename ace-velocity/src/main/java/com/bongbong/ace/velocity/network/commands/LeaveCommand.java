package com.bongbong.ace.velocity.network.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.VelocityCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import com.bongbong.ace.velocity.network.ALimboSessionHandler;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;

@RequiredArgsConstructor
public class LeaveCommand extends BaseCommand {
    private final Limbo limboServer;
    private final PlayerFinder playerFinder;
    private final RegisteredServer defaultServer;
    private final VelocityCommandManager velocityCommandManager;
    private final LimboFactory limboFactory;

    @CommandAlias("leave|hub|lobby|l")
    public void onCommand(CommandIssuer issuer) {
        if (!issuer.isPlayer()) return;
        Player issuerPlayer = playerFinder.getPlayer(issuer.getUniqueId());

        if (issuerPlayer.getCurrentServer().isPresent())
            limboServer.spawnPlayer(issuerPlayer, new ALimboSessionHandler(issuerPlayer, velocityCommandManager, defaultServer));
    }
}