package com.bongbong.ace.velocity.staff;

import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.shared.redis.RedisManager;
import com.bongbong.ace.velocity.PunishmentSettings;
import com.bongbong.ace.velocity.database.Mongo;
import com.bongbong.ace.velocity.database.MongoUpdate;
import com.bongbong.ace.velocity.staff.punishments.PunishmentManager;
import com.bongbong.ace.velocity.utils.Colors;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.bongbong.ace.velocity.utils.Registrar;
import com.bongbong.ace.velocity.utils.ServerBroadcaster;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.event.HoverEvent;
import org.bson.Document;

import java.util.*;
import java.util.logging.Logger;

public class StaffManager {
    private final Map<UUID, StaffProfile> profiles;
    private final Mongo mongo;
    private final PlayerFinder playerFinder;

    public StaffManager(Mongo mongo, PlayerFinder playerFinder,
                        VelocityCommandManager commandManager,
                        RedisManager redisManager, Registrar registrar,
                        ServerBroadcaster broadcaster, PunishmentSettings settings,
                        Logger logger) {
        this.mongo = mongo;
        this.playerFinder = playerFinder;
        this.profiles = new HashMap<>();

        commandManager.getCommandCompletions().registerCompletion("whitelist", (c) -> Arrays.asList(
                "staff", "media", "donors", "everyone"));

        registrar.registerListener(new StaffListener(this));
        redisManager.registerListener(new NotificationListener(this));

        PunishmentManager punishmentManager = new PunishmentManager(
                playerFinder, this, broadcaster, mongo, commandManager, settings,
                registrar, logger);
    }

    public void sendNotification(Notification notification) {
//        System.out.println(notification.getMessage() + " [" + notification.getHoverString() + "]");
        for (StaffProfile profile : getOnlineStaffProfiles()) {
            if (playerFinder.getPlayer(profile.getUuid()) == null) continue;
            if (!profile.getSettings().isStaffNotifications()) continue;

            switch (profile.getSettings().modeFromType(notification.getType())) {
                case NETWORK: break;
                case LOCAL: {
                    if (notification.getServer() == null) continue;
                    if (!playerFinder.getPlayersAtServer(notification.getServer())
                            .contains(playerFinder.getPlayer(profile.getUuid()))) continue;
                    break;
                }
                case DISABLED:
                default: continue;
            }

            Player player = playerFinder.getPlayer(profile.getUuid());

            player.sendMessage(Colors.get(
                    "&7&o[" + notification.getIssuerName() + ": " + notification.getMessage() + "&7&o]")
                    .hoverEvent(HoverEvent.hoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Colors.get(notification.getHoverString())
                    ))
            );
        }
    }

    protected StaffProfile createProfile(UUID uuid) {
        StaffProfile profile = new StaffProfile(uuid);
        profiles.put(profile.getUuid(), profile);

        return profile;
    }

    public Set<UUID> getOnlineStaff() {
        return profiles.keySet();
    }

    public Collection<StaffProfile> getOnlineStaffProfiles() {
        return profiles.values();
    }

    public StaffProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    protected void removeProfile(UUID uuid) {
        profiles.remove(uuid);
    }

    protected boolean isProfileCached(UUID uuid) {
        return profiles.containsKey(uuid);
    }

    protected StaffProfile pullProfile(UUID uuid, boolean store) {
        Document document = mongo.getDocument("staff_profiles", "_id",  uuid);

        if (document == null) return null;

        StaffProfile profile = new StaffProfile(uuid);
        profile.importFromDocument(document);

        if (store) profiles.put(profile.getUuid(), profile);

        return profile;
    }

    protected void pushProfile(StaffProfile profile, boolean unload) {
        MongoUpdate mu = new MongoUpdate("staff_profiles", profile.getUuid());
        mu.setUpdate(profile.exportToDocument());

        mongo.massUpdate(mu);

        if (unload) profiles.remove(profile.getUuid());
    }
}
