package com.bongbong.ace.velocity.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bson.Document;

import java.util.*;

public @Data class Profile {
    private final UUID uuid;
    private HashMap<String, Long> playTime;
    private Settings settings;
    private UUID lastRecipient;
    private List<String> donations;
    private List<UUID> ignored, ranks, badges, appliedBadges, friends, outgoingFriendRequests, incomingFriendRequests;
    private String name, currentIp, timeZone, country, countyCode, lastSeenServer;
    private Date chatCooldown, firstJoin, lastSeen;

    protected Profile(UUID uuid) {
        this.uuid = uuid;
        this.name = "Suzanne Crook";
        this.settings = new Settings();
        this.playTime = new HashMap<>();
        this.ignored = new ArrayList<>();
        this.ranks = new ArrayList<>();
        this.badges = new ArrayList<>();
        this.appliedBadges = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.outgoingFriendRequests = new ArrayList<>();
        this.incomingFriendRequests = new ArrayList<>();
        this.donations = new ArrayList<>();
    }

    protected void importFromDocument(Document document) {
        setCurrentIp(document.getString("current_ip"));

        settings.importFromDocument(document);

        setDonations(document.getList("donations", String.class));
        if (donations == null) setDonations(new ArrayList<>());

        setIgnored(document.getList("ignored", UUID.class));
        if (ignored == null) setIgnored(new ArrayList<>());

        setRanks(document.getList("ranks", UUID.class));
        if (ranks == null) setRanks(new ArrayList<>());

        setBadges(document.getList("badges", UUID.class));
        if (badges == null) setBadges(new ArrayList<>());

        setAppliedBadges(document.getList("appliedBadges", UUID.class));
        if (appliedBadges == null) setAppliedBadges(new ArrayList<>());

        setFriends(document.getList("friends", UUID.class));
        if (friends == null) setFriends(new ArrayList<>());

        setOutgoingFriendRequests(document.getList("outgoingRequests", UUID.class));
        if (outgoingFriendRequests == null) setOutgoingFriendRequests(new ArrayList<>());

        setIncomingFriendRequests(document.getList("incomingRequests", UUID.class));
        if (incomingFriendRequests == null) setIncomingFriendRequests(new ArrayList<>());

        List<String> playTimeRaw = document.getList("playTime", String.class);
        if (playTimeRaw != null && !playTimeRaw.isEmpty()) playTimeRaw.forEach(string -> {
            String[] strings = string.split(";");
            playTime.put(strings[0], Long.valueOf(strings[1]));
        });
    }

    protected Map<String, Object> exportToDocument() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("current_ip", currentIp);
        map.put("settings", settings.export());

        map.put("ignored", ignored);
        map.put("ranks", ranks);
        map.put("badges", badges);
        map.put("appliedBadges", appliedBadges);
        map.put("friends", friends);
        map.put("outgoingRequests", outgoingFriendRequests);
        map.put("incomingRequests", incomingFriendRequests);
        map.put("donations", donations);

        List<String> playTimeRaw = new ArrayList<>();
        playTime.forEach((server, time) -> playTimeRaw.add(server + ";" + time));
        map.put("playTime", playTimeRaw);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return map;
    }

    public String exportToJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(exportToDocument());
    }

    protected void importFromJson(String string) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Document document = (Document) gson.fromJson(string, Map.class);
        importFromDocument(document);
    }
}

