package com.bongbong.ace.velocity.utils;

import lombok.Data;

import java.util.UUID;

public @Data class OfflinePlayer {
    UUID uniqueId;
    String username;
}
