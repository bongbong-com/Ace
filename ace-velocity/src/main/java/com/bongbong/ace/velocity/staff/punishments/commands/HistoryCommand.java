package com.bongbong.ace.velocity.staff.punishments.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.ace.shared.utils.TimeUtil;
import com.bongbong.ace.velocity.staff.punishments.Punishment;
import com.bongbong.ace.velocity.staff.punishments.PunishmentManager;
import com.bongbong.ace.velocity.staff.punishments.PunishmentProfile;
import com.bongbong.ace.velocity.staff.punishments.menus.HistoryMenu;
import com.bongbong.ace.velocity.utils.Colors;
import com.bongbong.ace.velocity.utils.OfflinePlayer;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.cirrus.velocity.wrapper.VelocityPlayerWrapper;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@RequiredArgsConstructor
public class HistoryCommand extends BaseCommand {
    private final PlayerFinder playerFinder;
    private final PunishmentManager punishmentManager;

    @CommandCompletion("@players")
    @CommandAlias("history|c|check")
    @Syntax("<target> [-dump]")
    @SuppressWarnings("ignored")
    public void history(CommandIssuer issuer, PunishmentProfile target, @Optional String chatToggle) {
        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        List<Punishment> punishments = punishmentManager.getPunishmentsHistory(target);
        Player issuerPlayer = issuer.isPlayer() ? playerFinder.getPlayer(issuer.getUniqueId()) : null;

        if (issuerPlayer != null && (chatToggle == null || !chatToggle.contains("-dump"))) {
            TreeMap<Date, Punishment> map = new TreeMap<>();
            for (Punishment punishment : punishments) map.put(punishment.getIssued(), punishment);

            VelocityPlayerWrapper velocityPlayerWrapper = new VelocityPlayerWrapper(issuerPlayer);
            new HistoryMenu(playerFinder, map).display(velocityPlayerWrapper);
            return;
        }

        issuer.sendMessage(Colors.translate("&6--- " + oTarget.getUsername() + "'s History ---"));

        int count = 1;
        TreeMap<Date, Punishment> map = new TreeMap<>();
        for(Punishment punishment : punishments) {
            if (punishment.isActive() && punishment.getType() != Punishment.Type.KICK) {
                String staffName = null;
                if (punishment.getIssuer() == null) staffName = "Console";
                else {
                    OfflinePlayer staffPlayer = playerFinder.getOPlayerFromUUID(punishment.getIssuer());
                    if (staffPlayer != null) staffName = staffPlayer.getUsername();
                }

                if (issuerPlayer != null) issuerPlayer.sendMessage(Colors.get(
                        "&6&l" + count + ". &e&l" + punishment.getType().toString()
                        + "&e (" + punishment.originalDuration() + ") for &f&o" + punishment.getIssueReason() + "&e by " + staffName
                        + " (" + TimeUtil.formatTimeMillis(System.currentTimeMillis() - punishment.getIssued().getTime())
                        + " ago)").hoverEvent(
                                HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Colors.get(punishment.getId()))
                )); else issuer.sendMessage(Colors.translate(
                        "&6&l" + count + ". &e&l " + punishment.getId() + " " + punishment.getType().toString()
                        + "&e (" + punishment.originalDuration() + ") for &f&o" + punishment.getIssueReason() + "&e by " + staffName
                        + " (" + TimeUtil.formatTimeMillis(System.currentTimeMillis() - punishment.getIssued().getTime())
                        + " ago)"));
                count++;
                continue;
            }

            map.put(punishment.getIssued(), punishment);
        }

        for (Punishment punishment : map.descendingMap().values()) {
            String staffName = null;
            if (punishment.getIssuer() == null) staffName = "Console";
            else {
                OfflinePlayer staffPlayer = playerFinder.getOPlayerFromUUID(punishment.getIssuer());
                if (staffPlayer != null) staffName = staffPlayer.getUsername();
            }

            if (punishment.getType() == Punishment.Type.KICK) {
                if (issuerPlayer != null) issuerPlayer.sendMessage(Colors.get(
                        "&6" + count + ". &eKICK for &f&o" + punishment.getIssueReason() + "&e by " + staffName + " ("
                        + TimeUtil.formatTimeMillis(System.currentTimeMillis() - punishment.getIssued().getTime())
                        + " ago)").hoverEvent(
                        HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Colors.get(punishment.getId()))
                )); else issuer.sendMessage(Colors.translate("&6" + count + ". &e" + punishment.getId()
                        + " KICK for &f&o" + punishment.getIssueReason() + "&e by " + staffName + " ("
                        + TimeUtil.formatTimeMillis(System.currentTimeMillis() - punishment.getIssued().getTime())
                        + " ago)"));
                count++;
                continue;
            }

            String pardonerName = null;
            if (punishment.getPardoner() == null) pardonerName = "Console";
            else {
                OfflinePlayer pardonerPlayer = playerFinder.getOPlayerFromUUID(punishment.getPardoner());
                if (pardonerPlayer != null) pardonerName = pardonerPlayer.getUsername();
            }

            long pardon = punishment.getPardoned() == null ? System.currentTimeMillis() - punishment.getExpires().getTime()
                    : System.currentTimeMillis() - punishment.getPardoned().getTime();
            String pardonReason = punishment.getPardonReason() == null ? "Expired" : punishment.getPardonReason();

            if (issuerPlayer != null) issuerPlayer.sendMessage(Colors.get(
                    "&6" + count + ". &e" + punishment.getType().toString()
                    + " (" + punishment.originalDuration() + ") for &f&o" + punishment.getIssueReason() + "&e by " + staffName
                    + " (" + TimeUtil.formatTimeMillis(System.currentTimeMillis() - punishment.getIssued().getTime())
                    + " ago) un" + punishment.getType().pastMessage() + " for &f&o" + pardonReason + " &eby " + pardonerName
                    + " (" + TimeUtil.formatTimeMillis(pardon) + " ago).").hoverEvent(
                    HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Colors.get(punishment.getId()))
            )); else issuer.sendMessage(Colors.translate(
                    "&6" + count + ". &e" + punishment.getId() + " " + punishment.getType().toString()
                    + " (" + punishment.originalDuration() + ") for &f&o" + punishment.getIssueReason() + "&e by " + staffName
                    + " (" + TimeUtil.formatTimeMillis(System.currentTimeMillis() - punishment.getIssued().getTime())
                    + " ago) un" + punishment.getType().pastMessage() + " for &f&o" + pardonReason + " &eby " + pardonerName
                    + " (" + TimeUtil.formatTimeMillis(pardon) + " ago)."));
            count++;
        }

        issuer.sendMessage(Colors.translate("&6--- " + oTarget.getUsername() + "'s History ---"));
    }
}