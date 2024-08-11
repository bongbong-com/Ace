package com.bongbong.ace.velocity.staff;

import com.bongbong.ace.shared.utils.DateFormatter;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
public @Data class Notification {
    @RequiredArgsConstructor
    public enum Type {
        PUNISHMENT("punishmentAudit"),
        RANK("rankAudit"),
        SUDO("sudoAudit"),
        CHAT("chatAudit"),
        BASIC("basicAudits"),
        LEAVEJOIN("staffLeaveJoinNotification"),
        ACLOGS("anticheatLogs"),
        STAFFCHAT("staffChat"),
        REPORTS("reports");

        private @Getter final String name;
    }

    private final String issuerName;
    private final Type type;
    private final String message, server;
    private final List<String> hover;

    public String getHoverString() {
        if (hover == null) return DateFormatter.format(new Date());

        String hoverString = String.join("\n", hover);
        return DateFormatter.format(new Date()) + (hoverString.isEmpty() ? "" : "\n" + hoverString);
    }
}
