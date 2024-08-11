package com.bongbong.ace.spigot.commands;//package com.bongbong.ace.spigot.commands;
//
//import com.bongbong.ace.shared.redis.RedisManager;
//import co.aikar.commands.BaseCommand;
//import co.aikar.commands.annotation.*;
//import org.bukkit.Bukkit;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//@CommandAlias("gamemode")
//public class GamemodeCommand extends BaseCommand {
//    private final RedisManager redisManager;
//    private final String orginServer;
//
//    public GamemodeCommand(RedisManager redisManager, String orginServer) {
//        this.orginServer = orginServer;
//        this.redisManager = redisManager;
//    }
//
//    @Default
//    @Syntax("[target]")
//    @CommandCompletion("@players")
//    public void execute(CommandSender sender, String[] args) {
//        if (!sender.hasPermission("essentials.gamemode")) {
//            sender.sendMessage(Locale.NO_PERMISSION.format());
//            return;
//        }
//
//        Packet packet;
//        boolean isPlayer = sender instanceof Player;
//        Player senderPlayer = isPlayer ? (Player) sender : null;
//        String senderName = isPlayer ? senderPlayer.getName() : null;
//
//        switch (args.length) {
//            case 1: {
//                if (!isPlayer) {
//                    sender.sendMessage(Colors.get("&cConsole must specify at least 2 arguments."));
//                    return;
//                }
//
//                Player target = Bukkit.getPlayer(args[0]);
//                if (target == null) {
//                    sender.sendMessage(Locale.TARGET_NOT_ONLINE.format());
//                    return;
//                }
//
//                senderPlayer.teleport(target);
//                senderPlayer.sendMessage(Colors.get("&cYou teleported to " + target.getName()));
//
//                packet = new BasicCommandPacket(orginServer, senderName,
//                        "teleported to " + target.getName());
//            }
//            case 2: {
//                Player target1 = Bukkit.getPlayer(args[0]);
//                if (target == null) {
//                    sender.sendMessage(Locale.TARGET_NOT_ONLINE.format());
//                    return;
//                }
//
//                senderPlayer.teleport(target);
//                senderPlayer.sendMessage(Colors.get("&cYou teleported to " + target.getName()));
//
//                packet = new BasicCommandPacket(orginServer, senderName,
//                        "teleported to " + target.getName());
//            }
//        }
//
//
//
//        redisManager.sendPacket(packet);
//    }
//}
