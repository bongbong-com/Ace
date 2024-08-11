package com.bongbong.ace.velocity.profiles;

import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.shared.redis.RedisManager;
import com.bongbong.ace.shared.redis.packets.UpdatePermissionsPacket;
import com.bongbong.ace.velocity.Locale;
import com.bongbong.ace.velocity.database.Mongo;
import com.bongbong.ace.velocity.database.MongoUpdate;
import com.bongbong.ace.velocity.profiles.commands.ListCommand;
import com.bongbong.ace.velocity.profiles.ranks.Rank;
import com.bongbong.ace.velocity.profiles.ranks.RankManager;
import com.bongbong.ace.velocity.utils.OfflinePlayer;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.bongbong.ace.velocity.utils.Registrar;
import com.bongbong.ace.velocity.utils.TaskScheduler;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ProfileManager {
    private final @Getter Map<UUID, Profile> profiles;
    private final TaskScheduler scheduler;
    private final PlayerFinder finder;
    private final Mongo mongo;
    private final RedisManager redisManager;
    private final RankManager rankManager;
    private final Logger logger;

    public ProfileManager(TaskScheduler scheduler, Mongo mongo,
                          VelocityCommandManager commandManager, PlayerFinder finder,
                          Locale locale, RedisManager redisManager,
                          Registrar registrar, Logger logger) {

        this.profiles = new HashMap<>();
        this.scheduler = scheduler;
        this.mongo = mongo;
        this.redisManager = redisManager;
        this.finder = finder;
        this.logger = logger;

        commandManager.getCommandContexts().registerContext(Profile.class,
                (c) -> findProfile(c.popFirstArg(), false));

        registrar.registerListener(new ProfileListener(this));

        this.rankManager = new RankManager(mongo, commandManager, this, locale, registrar, scheduler);
        commandManager.registerCommand(new ListCommand(finder, this, rankManager));
    }

    public Profile createProfile(UUID uuid) {
        Profile profile = new Profile(uuid);
        profiles.put(profile.getUuid(), profile);

        scheduler.runTask(() -> setGeoData(uuid));

        return profile;
    }


    public Profile findProfile(String name, boolean dataBaseSearch) {
        Player target = finder.getPlayer(name);
        if (target != null) return getProfile(target.getUniqueId());

        OfflinePlayer oPlayer = finder.getOPlayerFromUsername(name);
        if (oPlayer == null) return null;

        return findProfile(oPlayer.getUniqueId(), dataBaseSearch);
    }

    public Profile findProfile(UUID uuid, boolean dataBaseSearch) {
        Profile profile = getProfile(uuid);
        if (profile != null) return profile;
        else if (dataBaseSearch) return pullProfile(uuid, false);

        return null;
    }

    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public void removeProfile(UUID uuid) {
        profiles.remove(uuid);
    }

    public boolean isProfileCached(UUID uuid) {
        return profiles.containsKey(uuid);
    }

    public void setGeoData(UUID uuid) {
        Profile profile = profiles.get(uuid);
        // logic for geo-location & timezone setting
    }

    public void updateAllProfiles() {
        for (Profile profile : profiles.values()) updateProfile(profile);
    }

    public void updateProfile(Profile profile) {
        Rank highestRank = rankManager.getHighestRankFromId(profile.getRanks());
        if (highestRank == null) return;

        List<String> permissions = rankManager.getAllPermissions(highestRank);
        String color = highestRank.getColor() == null ? "&f" : highestRank.getColor();
        String prefix = highestRank.getPrefix() == null ? "" : highestRank.getPrefix();
        redisManager.sendPacket(new UpdatePermissionsPacket(profile.getUuid(), permissions, color, prefix));
    }

    public Profile pullProfile(UUID uuid, boolean store) {
        Document document = mongo.getDocument("profiles", "_id",  uuid);

        if (document == null) return null;

        Profile profile = new Profile(uuid);
        profile.importFromDocument(document);

        if (store) profiles.put(profile.getUuid(), profile);

        return profile;
    }

    public void pushProfile(Profile profile, boolean unload) {
        MongoUpdate mu = new MongoUpdate("profiles", profile.getUuid());
        mu.setUpdate(profile.exportToDocument());

        mongo.massUpdate(mu);

        if (unload) profiles.remove(profile.getUuid());
    }
}
