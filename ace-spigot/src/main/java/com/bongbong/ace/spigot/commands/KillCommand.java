package com.bongbong.ace.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.bongbong.ace.shared.redis.RedisManager;
import com.bongbong.ace.shared.redis.packets.BasicCommandPacket;
import com.bongbong.ace.spigot.Locale;
import com.bongbong.ace.spigot.utils.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("kill")
public class KillCommand extends BaseCommand {
    private final RedisManager redisManager;
    private final String orginServer;

    public KillCommand(RedisManager redisManager, String orginServer) {
        this.orginServer = orginServer;
        this.redisManager = redisManager;
    }

    @Default
    @Syntax("[target]")
    @CommandCompletion("@players")
    public void execute(CommandSender sender, @Optional Player player) {
        if (!sender.hasPermission("essentials.kill")) {
            sender.sendMessage(Locale.NO_PERMISSION.format());
            return;
        }

        if (player == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Console must specify a target.");
                return;
            }

            player = (Player) sender;
        }

        player.setHealth(0);

        if (player != sender) sender.sendMessage(Colors.translate("&aYou killed &f" + player.getName() + "&a."));

        player.sendMessage(Colors.translate("&aYou have been killed."));

        String senderName = sender instanceof Player ? sender.getName() : null;
        redisManager.sendPacket(new BasicCommandPacket(orginServer, senderName,
                "killed " + player.getName()));
    }
}
