package com.bongbong.ace.velocity.network;

import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.velocity.network.commands.LeaveCommand;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.file.BuiltInWorldFileType;
import net.elytrium.limboapi.api.file.WorldFile;
import net.elytrium.limboapi.api.player.GameMode;

import java.io.IOException;
import java.nio.file.Path;

public class NetworkManager {

    public NetworkManager(LimboFactory factory, Path dataDirectory,
                          RegisteredServer defaultServer, VelocityCommandManager commandManager,
                          PlayerFinder playerFinder) {

        VirtualWorld authWorld = factory.createVirtualWorld(
                Dimension.OVERWORLD,
                2, 91, 1,
                (float) 180, (float) 0
        );

        try {
            Path path = dataDirectory.resolve("world.schematic");
            WorldFile file = factory.openWorldFile(BuiltInWorldFileType.SCHEMATIC, path);

            file.toWorld(factory, authWorld, 0, 90, 0, 15);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        Limbo limboServer = factory
                .createLimbo(authWorld)
                .setName("LimboAuth")
                .setWorldTime(1000L)
                .setGameMode(GameMode.ADVENTURE);

        commandManager.registerCommand(new LeaveCommand(
                limboServer, playerFinder, defaultServer, commandManager, factory));
    }
}
