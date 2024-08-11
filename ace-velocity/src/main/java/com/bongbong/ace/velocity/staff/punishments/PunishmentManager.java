package com.bongbong.ace.velocity.staff.punishments;

import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.shared.utils.DateFormatter;
import com.bongbong.ace.velocity.PunishmentSettings;
import com.bongbong.ace.velocity.database.Mongo;
import com.bongbong.ace.velocity.database.MongoUpdate;
import com.bongbong.ace.velocity.staff.Notification;
import com.bongbong.ace.velocity.staff.StaffManager;
import com.bongbong.ace.velocity.staff.punishments.commands.HistoryCommand;
import com.bongbong.ace.velocity.staff.punishments.commands.PunishCommand;
import com.bongbong.ace.velocity.staff.punishments.commands.PunishmentCommands;
import com.bongbong.ace.velocity.staff.punishments.commands.UnpunishmentCommands;
import com.bongbong.ace.velocity.utils.*;
import com.velocitypowered.api.proxy.Player;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.Document;

import java.util.*;
import java.util.logging.Logger;

public class PunishmentManager {
    private final PlayerFinder finder;
    private final StaffManager staffManager;
    private final ServerBroadcaster broadcaster;
    private final Mongo mongo;
    private final PunishmentSettings settings;
    private final Logger logger;
    private final Map<UUID, PunishmentProfile> profiles;

    public PunishmentManager(PlayerFinder finder, StaffManager staffManager,
                             ServerBroadcaster broadcaster, Mongo mongo,
                             VelocityCommandManager commandManager, PunishmentSettings settings,
                             Registrar registrar, Logger logger) {

        this.finder = finder;
        this.staffManager = staffManager;
        this.broadcaster = broadcaster;
        this.mongo = mongo;
        this.settings = settings;
        this.profiles = new HashMap<>();
        this.logger = logger;

        PunishmentUtilities utilities = new PunishmentUtilities(settings);

        commandManager.getCommandContexts().registerContext(PunishmentProfile.class,
                (c) -> findProfile(c.popFirstArg(), true));

        commandManager.registerCommand(new PunishmentCommands(finder, this, utilities));
        commandManager.registerCommand(new UnpunishmentCommands(finder, this, utilities));
        commandManager.registerCommand(new HistoryCommand(finder, this));
        commandManager.registerCommand(new PunishCommand(finder, this, settings, utilities));

        registrar.registerListener(new PunishmentListener(this, settings));
    }

    public Punishment createPunishment(Punishment.Type type, PunishmentProfile subjectProfile, OfflinePlayer subject,
                                       OfflinePlayer issuer, String reason, Date expires, boolean broadcast,
                                       String category, String serverName) {

        for (Punishment punishment : getPunishments(subjectProfile, type))
            if (punishment.isActive() && !type.equals(Punishment.Type.KICK)) return null;

        Punishment punishment = new Punishment(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        punishment.setType(type);
        punishment.setVictim(subjectProfile.getUuid());
        punishment.setIssuer(issuer == null ? null : issuer.getUniqueId());
        punishment.setIssueReason(reason);
        punishment.setIssued(new Date());
        punishment.setExpires(expires);
        punishment.setBroadcast(broadcast);
        punishment.setCategory(category);

        subjectProfile.getPunishments().add(punishment.getId());

        Player subjectPlayer = finder.getPlayer(subjectProfile.getUuid());

        if (subjectPlayer != null) {
            if (punishment.getType() == Punishment.Type.MUTE) subjectPlayer.sendMessage(Colors.get("&cYou have been muted."));
            else subjectPlayer.disconnect(Colors.get(String.join("\n", punishment.getMessage(settings))));
        } else pushProfile(subjectProfile, false);

        String issuerName = issuer == null ? "Console" : issuer.getUsername();
        List<String> hover = Arrays.asList(
                "",
                "ID: #" + punishment.getId(),
                "",
                "REASON: " + punishment.getIssueReason(),
                "DURATION: " + punishment.originalDuration(),
                "",
                "EXPIRY: " + punishment.expiry(),
                "BROADCASTED: " + punishment.isBroadcast(),
                "TYPE: " + type.toString().toUpperCase(),
                "TARGET: " + subject.getUsername(),
                "ISSUER: " + issuerName,
                "",
                "SERVER: " + punishment.getOrginServer(),
                "CATEGORY: " + punishment.getCategory());

        if (broadcast) broadcaster.broadcast("&c&l" + issuerName + " has " + punishment.typeMessage() + " " + subject.getUsername());

        Notification notification = new Notification(
                issuerName, Notification.Type.PUNISHMENT, punishment.typeMessage() + " " + subject.getUsername(), serverName, hover);
        staffManager.sendNotification(notification);

        pushPunishment(punishment);
        return punishment;
    }

    public void pushPunishment(Punishment punishment) {
        MongoUpdate mu = new MongoUpdate("punishments", punishment.getId());
        mu.setUpdate(punishment.exportToDocument());
        mongo.massUpdate(mu);
    }

    public Punishment pullPunishment(String id) {
        Document document = mongo.getDocument("punishments", "_id",  id);

        if (document == null) return null;

        Punishment punishment = new Punishment(id);
        punishment.importFromDocument(document);

        return punishment;
    }

    public List<Punishment> getPunishments(PunishmentProfile punishmentProfile, Punishment.Type type) {
        List<Punishment> punishments = new ArrayList<>();
        for (String id : punishmentProfile.getPunishments()) {
            Punishment punishment = pullPunishment(id);
            if (punishment != null && punishment.getType().equals(type)) punishments.add(punishment);
        }
        return punishments;
    }

    public List<Punishment> getPunishmentsHistory(PunishmentProfile profile) {
        List<Punishment> punishments = new ArrayList<>();
        for (String id : profile.getPunishments()) {
            Punishment punishment = pullPunishment(id);
            if (punishment != null) punishments.add(punishment);
        }
        return punishments;
    }

    public Punishment getActivePunishment(PunishmentProfile punishmentProfile, Punishment.Type type) {
        for (Punishment punishment : getPunishments(punishmentProfile, type)) {
            if (punishment.isActive()) return punishment;
        }
        return null;
    }

    public void pardonActivePunishment(PunishmentProfile profile, Punishment.Type type, OfflinePlayer subject,
                                       OfflinePlayer issuer, String reason, boolean broadcast, String serverName) {

        Punishment punishment = getActivePunishment(profile, type);
        punishment.setPardoned(new Date());
        punishment.setPardoner(issuer == null ? null : issuer.getUniqueId());
        punishment.setPardonReason(reason);

        String issuerName = issuer == null ? "Console" : issuer.getUsername();
        List<String> hover = Arrays.asList(
                "",
                "ID: #" + punishment.getId() + "(" + (type.toString().toUpperCase()) + ")",
                "TARGET: " + subject.getUsername(),
                "",
                "ORIGINAL (" + DateFormatter.format(punishment.getIssued()) + "):",
                "> REASON: " + punishment.getIssueReason(),
                "> DURATION: " + punishment.originalDuration(),
                "> EXPIRY: " + punishment.expiry(),
                "> ISSUER: " + issuerName,
                "> BROADCASTED: " + punishment.isBroadcast(),
                "> SERVER: " + punishment.getOrginServer(),
                "> CATEGORY: " + punishment.getCategory(),
                "",
                "PARDON (" + DateFormatter.format(punishment.getPardoned()) + "):",
                "> REASON: " + punishment.getPardonReason(),
                "> ISSUER: " + punishment.getPardoner(),
                "> BROADCASTED: " + broadcast,
                "> SERVER: " + serverName,
                "");

        if (broadcast) broadcaster.broadcast("&c&l" + issuerName + " has " + punishment.typeMessage() + " " + subject.getUsername());

        Notification notification = new Notification(issuerName, Notification.Type.PUNISHMENT, punishment.typeMessage(), serverName, hover);
        staffManager.sendNotification(notification);

        pushPunishment(punishment);
    }

    public PunishmentProfile createProfile(UUID uuid) {
        PunishmentProfile punishmentProfile = new PunishmentProfile(uuid);
        profiles.put(punishmentProfile.getUuid(), punishmentProfile);

        return punishmentProfile;
    }

    public PunishmentProfile findProfile(String name, boolean dataBaseSearch) {
        Player target = finder.getPlayer(name);
        if (target != null) return getProfile(target.getUniqueId());

        OfflinePlayer oPlayer = finder.getOPlayerFromUsername(name);
        if (oPlayer == null) return null;

        return findProfile(oPlayer.getUniqueId(), dataBaseSearch);
    }

    public PunishmentProfile findProfile(UUID uuid, boolean dataBaseSearch) {
        PunishmentProfile profile = getProfile(uuid);
        if (profile != null) return profile;
        else if (dataBaseSearch) return pullProfile(uuid, false);

        return null;
    }

    public PunishmentProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public void removeProfile(UUID uuid) {
        profiles.remove(uuid);
    }

    public boolean isProfileCached(UUID uuid) {
        return profiles.containsKey(uuid);
    }

    public PunishmentProfile pullProfile(UUID uuid, boolean store) {
        Document document = mongo.getDocument("punishment_profiles", "_id",  uuid);

        if (document == null) return null;

        PunishmentProfile punishmentProfile = new PunishmentProfile(uuid);
        punishmentProfile.importFromDocument(document);

        if (store) profiles.put(punishmentProfile.getUuid(), punishmentProfile);

        return punishmentProfile;
    }

    public void pushProfile(PunishmentProfile punishmentProfile, boolean unload) {
        MongoUpdate mu = new MongoUpdate("punishment_profiles", punishmentProfile.getUuid());
        mu.setUpdate(punishmentProfile.exportToDocument());

        mongo.massUpdate(mu);

        if (unload) profiles.remove(punishmentProfile.getUuid());
    }
}