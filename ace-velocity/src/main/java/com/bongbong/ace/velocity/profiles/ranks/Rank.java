package com.bongbong.ace.velocity.profiles.ranks;

import lombok.Data;
import org.bson.Document;

import java.util.*;

public @Data class Rank {
    final UUID uuid;
    String name, displayName, prefix, color;
    int weight;
    boolean defaultRank;
    List<UUID> parents;
    List<String> permissions, descriptionLines;

    protected Rank(UUID uuid) {
        this.uuid = uuid;
        this.parents = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.descriptionLines = new ArrayList<>();
        this.color = "&f";
    }

    protected Map<String, Object> exportToDocument() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("displayName", displayName);
        map.put("prefix", prefix);
        map.put("color", color);
        map.put("weight", weight);
        map.put("defaultRank", defaultRank);
        map.put("parents", parents);
        map.put("permissions", permissions);
        map.put("description", descriptionLines);

        return map;
    }

    protected void importFromDocument(Document document) {
        setName(document.getString("name"));
        setDisplayName(document.getString("displayName"));
        setPrefix(document.getString("prefix"));
        setColor(document.getString("color"));
        setWeight(document.getInteger("weight"));
        setDefaultRank(document.getBoolean("defaultRank"));
        setParents(document.getList("parents", UUID.class));
        if (parents == null) setParents(new ArrayList<>());

        setPermissions(document.getList("permissions", String.class));
        if (permissions == null) setPermissions(new ArrayList<>());

        setDescriptionLines(document.getList("description", String.class));
        if (descriptionLines == null) setDescriptionLines(new ArrayList<>());
    }
}
