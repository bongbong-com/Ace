package com.bongbong.ace.velocity.network;

import co.aikar.commands.RootCommand;
import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.velocity.utils.Colors;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

@RequiredArgsConstructor
public class ALimboSessionHandler implements LimboSessionHandler {
    private final Player proxyPlayer;
    private final VelocityCommandManager commandManager;
    private final RegisteredServer defaultServer;
    private LimboPlayer limboPlayer;

    private BossBar bossBar;

    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        this.limboPlayer = player;

        bossBar = BossBar.bossBar(
                Component.empty(),
                1.0F,
                BossBar.Color.WHITE,
                BossBar.Overlay.NOTCHED_20
        );
        bossBar.name(Colors.get("Please wait - this is TODO"));

        proxyPlayer.showBossBar(bossBar);
        proxyPlayer.sendMessage(Colors.get("&aWelcome to the Limbo. This is a work in progress."));
    }

    @Override
    public void onChat(String message) {
        if (!message.startsWith("/")) return;

        String[] args = message.split(" ");
        String[] modifiedArgs = Arrays.copyOfRange(args, 1, args.length);

        if (args[0].equals("/leave")) {
            limboPlayer.disconnect(defaultServer);
            return;
        }
        
        for (RootCommand rootCommand : commandManager.getRegisteredRootCommands()){
            if (("/" + rootCommand.getCommandName()).equals(args[0])) {
                rootCommand.execute(commandManager.getCommandIssuer(proxyPlayer), args[0], modifiedArgs);
                return;
            }
        }

        proxyPlayer.sendMessage(Colors.get("&cCommand not found."));
    }

    @Override
    public void onDisconnect() {
        proxyPlayer.hideBossBar(bossBar);
    }
}
