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
import java.util.UUID;

@RequiredArgsConstructor
public class UnpunishmentCommands extends BaseCommand {
    private final PlayerFinder playerFinder;
    private final PunishmentManager punishmentManager;
    private final PunishmentUtilities utilities;

    @CommandCompletion("@players")
    @CommandAlias("unban")
    @Syntax("<target> [reason] [-b]")
    @SuppressWarnings("ignored")
    public void unban(CommandIssuer sender, PunishmentProfile target,
                    @Default("Unspecified") String reason) {

        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.BAN;

        if (punishmentManager.getActivePunishment(target, type) == null) {
            sender.sendMessage(Colors.translate(
                    "&cThe target you specified does not have an active ban applied."));
            return;
        }

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(reason.split(" "), false);
        reason = entry.getValue();
        boolean broadcast = entry.getKey();

        sender.sendMessage(Colors.translate(
                "&aYou unbanned &f" + oTarget.getUsername() + "&a for &f" + reason));

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

        punishmentManager.pardonActivePunishment(target, type, oTarget, issuerPlayer, reason, broadcast, server);
    }

    @CommandCompletion("@players")
    @CommandAlias("unblacklist")
    @Syntax("<target> [reason] [-b]")
    @SuppressWarnings("ignored")
    public void unblacklist(CommandIssuer sender, PunishmentProfile target,
                            @Default("Unspecified") String reason) {

        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.BLACKLIST;

        if (punishmentManager.getActivePunishment(target, type) == null) {
            sender.sendMessage(Colors.translate(
                    "&cThe target you specified does not have an active blacklist applied."));
            return;
        }

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(reason.split(" "), false);
        reason = entry.getValue();
        boolean broadcast = entry.getKey();

        sender.sendMessage(Colors.translate(
                "&aYou unblacklisted &f" + oTarget.getUsername() + "&a for &f" + reason));

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

        punishmentManager.pardonActivePunishment(target, type, oTarget, issuerPlayer, reason, broadcast, server);
    }

    @CommandCompletion("@players")
    @CommandAlias("unmute")
    @Syntax("<target> [reason] [-b]")
    @SuppressWarnings("ignored")
    public void unmute(CommandIssuer sender, PunishmentProfile target,
                       @Default("Unspecified") String reason) {

        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        Punishment.Type type = Punishment.Type.MUTE;

        if (punishmentManager.getActivePunishment(target, type) == null) {
            sender.sendMessage(Colors.translate(
                    "&cThe target you specified does not have an active mute applied."));
            return;
        }

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(reason.split(" "), false);
        reason = entry.getValue();
        boolean broadcast = entry.getKey();


        sender.sendMessage(Colors.translate(
                "&aYou unmuted &f" + oTarget.getUsername() + "&a for &f" + reason));

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

        punishmentManager.pardonActivePunishment(target, type, oTarget, issuerPlayer, reason, broadcast, server);
    }
}
