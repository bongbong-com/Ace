package com.bongbong.ace.velocity.staff.punishments.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.ace.velocity.staff.punishments.Punishment;
import com.bongbong.ace.velocity.staff.punishments.PunishmentManager;
import com.bongbong.ace.velocity.staff.punishments.PunishmentProfile;
import com.bongbong.ace.velocity.staff.punishments.PunishmentUtilities;
import com.bongbong.ace.velocity.utils.Colors;
import com.bongbong.ace.velocity.utils.OfflinePlayer;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;
import java.util.Date;
import java.util.UUID;

import static com.bongbong.ace.shared.utils.TimeUtil.getTime;

@RequiredArgsConstructor
public class PunishmentCommands extends BaseCommand {
    private final PlayerFinder playerFinder;
    private final PunishmentManager punishmentManager;
    private final PunishmentUtilities utilities;
    
    @CommandCompletion("@players")
    @CommandAlias("ban|b")
    @Syntax("<target> [time] [reason] [-b]")
    @SuppressWarnings("ignored")
    public void ban(CommandIssuer sender, PunishmentProfile target,
                    @Default("Unspecified") String[] args) {

        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.BAN;

        if (punishmentManager.getActivePunishment(target, type) != null) {
            sender.sendMessage(Colors.translate(
                    "&cThe target you specified already has an active ban applied."));
            return;
        }

        Date expiration = getTime(args[0]);

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(args, expiration != null);
        String reason = entry.getValue();
        boolean broadcast = entry.getKey();

        sender.sendMessage(Colors.translate(
                "&aYou " + (expiration != null ? "temporarily" : "permanently")
                        + " banned &f" + oTarget.getUsername() + "&a for &f" + reason));

        OfflinePlayer issuerPlayer;
        String server = null;
        if (issuer == null) issuerPlayer = null;
        else {
            Player player = playerFinder.getPlayer(issuer);

            issuerPlayer = new OfflinePlayer();
            issuerPlayer.setUniqueId(issuer);
            issuerPlayer.setUsername(player.getUsername());
            server = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : null;
        }

        String category = utilities.getCategory(reason);

        punishmentManager.createPunishment(type, target, oTarget, issuerPlayer, reason, expiration, broadcast, category, server);
    }

    @CommandCompletion("@players")
    @CommandAlias("blacklist")
    @Syntax("<target> [reason] [-b]")
    @SuppressWarnings("ignored")
    public void blacklist(CommandIssuer sender, PunishmentProfile target,
                          @Default("Unspecified") String reason) {

        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.BLACKLIST;

        if (punishmentManager.getActivePunishment(target, type) != null) {
            sender.sendMessage(Colors.translate(
                    "&cThe target you specified already has an active blacklist applied."));
            return;
        }

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(reason.split(" "), false);
        reason = entry.getValue();
        boolean broadcast = entry.getKey();


        sender.sendMessage(Colors.translate(
                "&aYou blacklisted &f" + oTarget.getUsername() + "&a for &f" + reason));

        OfflinePlayer issuerPlayer;
        String server = null;
        if (issuer == null) issuerPlayer = null;
        else {
            Player player = playerFinder.getPlayer(issuer);

            issuerPlayer = new OfflinePlayer();
            issuerPlayer.setUniqueId(issuer);
            issuerPlayer.setUsername(player.getUsername());
            server = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : null;
        }

        String category = utilities.getCategory(reason);

        punishmentManager.createPunishment(type, target, oTarget, issuerPlayer, reason, null, broadcast, category, server);
    }

    @CommandCompletion("@players")
    @CommandAlias("mute")
    @Syntax("<target> [time] [reason] [-b]")
    @SuppressWarnings("ignored")
    public void mute(CommandIssuer sender, PunishmentProfile target,
                     @Default("Unspecified") String[] args) {

        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.MUTE;

        if (punishmentManager.getActivePunishment(target, type) != null) {
            sender.sendMessage(Colors.translate(
                    "&cThe target you specified already has an active mute applied."));
            return;
        }

        Date expiration = getTime(args[0]);

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(args, expiration != null);
        String reason = entry.getValue();
        boolean broadcast = entry.getKey();

        sender.sendMessage(Colors.translate(
                "&aYou " + (expiration != null ? "temporarily" : "permanently")
                        + " muted &f" + oTarget.getUsername() + "&a for &f" + reason));

        OfflinePlayer issuerPlayer;
        String server = null;
        if (issuer == null) issuerPlayer = null;
        else {
            Player player = playerFinder.getPlayer(issuer);

            issuerPlayer = new OfflinePlayer();
            issuerPlayer.setUniqueId(issuer);
            issuerPlayer.setUsername(player.getUsername());
            server = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : null;
        }

        String category = utilities.getCategory(reason);

        punishmentManager.createPunishment(type, target, oTarget, issuerPlayer, reason, expiration, broadcast, category, server);
    }

    @CommandCompletion("@players")
    @CommandAlias("kick")
    @Syntax("<target> [reason] [-b]")
    @SuppressWarnings("ignored")
    public void kick(CommandIssuer sender, PunishmentProfile target,
                     @Default("Unspecified") String reason) {

        if (target == null) return;

        if (playerFinder.getPlayer(target.getUuid()) == null) {
            sender.sendMessage(Colors.translate("&cYou may only kick online players."));
            return;
        }

        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.KICK;

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(reason.split(" "), false);
        reason = entry.getValue();
        boolean broadcast = entry.getKey();

        sender.sendMessage(Colors.translate(
                "&aYou kicked &f" + oTarget.getUsername() + "&a for &f" + reason));

        OfflinePlayer issuerPlayer;
        String server = null;
        if (issuer == null) issuerPlayer = null;
        else {
            Player player = playerFinder.getPlayer(issuer);

            issuerPlayer = new OfflinePlayer();
            issuerPlayer.setUniqueId(issuer);
            issuerPlayer.setUsername(player.getUsername());
            server = player.getCurrentServer().isPresent() ? player.getCurrentServer().get().getServerInfo().getName() : null;
        }

        String category = utilities.getCategory(reason);

        punishmentManager.createPunishment(type, target, oTarget, issuerPlayer, reason, null, broadcast, category, server);
    }
}
