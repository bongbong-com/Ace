package com.bongbong.ace.velocity.staff.punishments.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.ace.velocity.PunishmentSettings;
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
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PunishCommand extends BaseCommand {
    private final PlayerFinder playerFinder;
    private final PunishmentManager punishmentManager;
    private final PunishmentSettings settings;
    private final PunishmentUtilities utilities;

    @CommandCompletion("@players")
    @CommandAlias("punish|p|pu")
    @Syntax("<target> [reason] [-b]")
    @SuppressWarnings("ignored")
    public void punish(CommandIssuer sender, PunishmentProfile target, String reason) {
        if (target == null) return;
        OfflinePlayer oTarget = playerFinder.getOPlayerFromUUID(target.getUuid());

        UUID issuer = null;
        if (sender.isPlayer()) issuer = sender.getUniqueId();

        AbstractMap.SimpleEntry<Boolean, String> entry = utilities.broadcast(reason.split(" "), false);
        reason = entry.getValue();
        boolean broadcast = entry.getKey();

        String category = null;
        int index = 0;
        String[] list = settings.KEYWORDS.split(";");

        outerloop:
        for (String keywords : list) {
            String[] wordList = keywords.split(",");

            for (String keyword : wordList) {
                if (reason.contains(keyword)) {
                    category = settings.REASONS.split(";")[index];
                    break outerloop;
                }
            }

            index++;
        }

        if (category == null) {
            sender.sendMessage(Colors.translate(
                    "&cYour reason did not include any keywords to help us categorize this punishment." +
                            "\n&cPlease try again and provide a more descriptive reason."));
            return;
        }

        String punishmentsString = settings.PUNISHMENTS.split(";")[index];
        String[] punishmentsStrings = punishmentsString.split(",");

        int offense = 0;
        for (Punishment punishment : punishmentManager.getPunishmentsHistory(target)) {
            if (punishment.getCategory() == null) continue;
            if (punishment.getCategory().equals(category)) offense++;
        }


        String punishmentString;
        try {
            punishmentString = punishmentsStrings[offense];
        } catch (ArrayIndexOutOfBoundsException exception) {
            punishmentString = punishmentsStrings[punishmentsStrings.length - 1];
        }

        String[] newString = punishmentString.split(":");

        Punishment.Type type;
        switch (newString[0]) {
            case "ban":
                type = Punishment.Type.BAN;
                break;
            case "blacklist":
                type = Punishment.Type.BLACKLIST;
                break;
            case "kick":
                type = Punishment.Type.KICK;
                break;
            case "mute":
                type = Punishment.Type.MUTE;
                break;
            default:
                sender.sendMessage("&cSomething bad happened! Please report to a developer.");
                return;
        }

        if (punishmentManager.getActivePunishment(target, type) != null) {
            sender.sendMessage(
                    "&cThe target you specified already has an active "
                            + type.toString().toLowerCase() + " applied.");
            return;
        }

        Date expiration = getExpiration(newString[1]);

        sender.sendMessage(Colors.translate(
                "&aYou " + (type == Punishment.Type.KICK || type == Punishment.Type.BLACKLIST ? ""
                        : (expiration != null ? "temporarily " : "permanently ")) + type.pastMessage()
                        + " &f" + oTarget.getUsername() + "&a for &f" + reason));

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

        punishmentManager.createPunishment(type, target, oTarget, issuerPlayer, reason, expiration, broadcast, category, server);
    }

    private Date getExpiration(String splitString) {
        if (splitString.equals("permanent") || splitString.equals("perm")) {
            return null;
        }

        Pattern p = Pattern.compile("[a-z]+|\\d+");
        Matcher m = p.matcher(splitString.toLowerCase());

        int time = -1;
        String type = null;
        boolean b = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        while (m.find()) {
            String a = m.group();
            try {
                time = Integer.parseInt(a);
                if(time < 1) {
                    time = -1;
                }
            } catch(NumberFormatException e) {
                type = a;
            }

            if(time > 0 && type != null) {
                switch(type) {
                    case "seconds": case "second": case "sec": case "s":
                        calendar.add(Calendar.SECOND, time);
                        break;
                    case "minutes": case "minute": case "m":
                        calendar.add(Calendar.MINUTE, time);
                        break;
                    case "hours": case "hrs": case "hr": case "h":
                        calendar.add(Calendar.HOUR, time);
                        break;
                    case "days": case "day": case "d":
                        calendar.add(Calendar.HOUR, time * 24);
                        break;
                    case "weeks": case "week": case "w":
                        calendar.add(Calendar.HOUR, time * 24 * 7);
                        break;
                    case "months": case "month": case "mo":
                        calendar.add(Calendar.MONTH, time);
                        break;
                }

                b = true;
                time = -1;
                type = null;
            }
        }

        if (b) {
            return calendar.getTime();
        } else {
            return null;
        }
    }
}
