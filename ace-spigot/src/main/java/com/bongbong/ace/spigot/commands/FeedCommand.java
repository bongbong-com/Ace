package com.bongbong.ace.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.bongbong.ace.shared.redis.RedisManager;
import com.bongbong.ace.shared.redis.packets.BasicCommandPacket;
import com.bongbong.ace.spigot.Locale;
import com.bongbong.ace.spigot.utils.Colors;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("feed")
@RequiredArgsConstructor
public class FeedCommand extends BaseCommand {
    final RedisManager redisManager;
    final String orginServer;

    @Default
    @Syntax("[target]")
    @CommandCompletion("@players")
    public void execute(CommandSender sender, @Optional Player player) {
        if (!sender.hasPermission("essentials.feed")) {
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

        player.setFoodLevel(20);

        if (player != sender) sender.sendMessage(Colors.translate("&aYou fed &f" + player.getName() + "&a."));

        player.sendMessage(Colors.translate("&aYou have been fed."));

        String senderName = sender instanceof Player ? sender.getName() : null;
        redisManager.sendPacket(new BasicCommandPacket(orginServer, senderName,
                "fed " + player.getName()));
    }
}
