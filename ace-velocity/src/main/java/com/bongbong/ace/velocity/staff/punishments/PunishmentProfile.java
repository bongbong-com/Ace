package com.bongbong.ace.velocity.staff.punishments;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.bson.Document;

import java.util.*;

public @Data class PunishmentProfile {
    private final UUID uuid;
    private Date muteExpiration;
    private List<String> punishments, ipHistory;

    public PunishmentProfile(UUID uuid) {
        this.uuid = uuid;

        this.punishments = new ArrayList<>();
        this.ipHistory = new ArrayList<>();
    }

    protected void addIp(String ip) {
        ipHistory.add(ip);
    }

    protected void importFromDocument(Document document) {
        setPunishments(document.getList("punishments", String.class));
        if (punishments == null) setPunishments(new ArrayList<>());

        setIpHistory(document.getList("ipHistory", String.class));
        if (ipHistory == null) setIpHistory(new ArrayList<>());
    }

    protected Map<String, Object> exportToDocument() {
        Map<String, Object> map = new HashMap<>();
        map.put("punishments", punishments);
        map.put("ipHistory", ipHistory);

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
