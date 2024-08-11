package com.bongbong.ace.velocity.staff;

import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public @Data class StaffProfile {
    private final UUID uuid;
    private StaffSettings settings;
    private boolean vanished;

    protected StaffProfile(UUID uuid) {
        this.uuid = uuid;
        this.settings = new StaffSettings();
        this.vanished = false;
    }

    protected void importFromDocument(Document document) {
        settings.importFromDocument(document);
    }

    protected Map<String, Object> exportToDocument() {
        Map<String, Object> map = new HashMap<>();
        map.put("settings", settings.export());

        return map;
    }
}
