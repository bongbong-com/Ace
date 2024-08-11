package com.bongbong.ace.velocity.database;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public @Data class MongoUpdate {
    private final String collectionName;
    private final Object id;
    private Map<String, Object> update = new HashMap<>();

    public MongoUpdate put(String key, Object value) {
        update.put(key, value);
        return this;
    }
}
