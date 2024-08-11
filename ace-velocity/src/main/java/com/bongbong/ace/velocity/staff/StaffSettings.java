package com.bongbong.ace.velocity.staff;

import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public @Data class StaffSettings {
    public enum Mode {
        NETWORK,
        LOCAL,
        DISABLED
    }

    private boolean staffNotifications = true;
    private Mode punishmentAudit = Mode.NETWORK, rankAudit = Mode.NETWORK, sudoAudit = Mode.NETWORK,
            chatAudit = Mode.NETWORK, basicAudits = Mode.NETWORK, staffLeaveJoinNotification = Mode.NETWORK,
            anticheatLogs = Mode.NETWORK, staffChat = Mode.NETWORK, reports = Mode.NETWORK;

    protected List<String> export() {
        List<String> list = new ArrayList<>();

        list.add("staffNotifications;" + staffNotifications);
        list.add("punishmentAudit;" + punishmentAudit.toString());
        list.add("rankAudit;" + rankAudit.toString());
        list.add("sudoAudit;" + sudoAudit.toString());
        list.add("chatAudit;" + chatAudit.toString());
        list.add("basicAudits;" + basicAudits.toString());
        list.add("staffLeaveJoinNotification;" + staffLeaveJoinNotification.toString());
        list.add("anticheatLogs;" + anticheatLogs.toString());
        list.add("staffChat;" + staffChat.toString());
        list.add("reports;" + reports.toString());

        return list;
    }

    protected void importFromDocument(Document document) {
        List<String> rawSettings = document.getList("settings", String.class);
        for (String rawString : rawSettings) {
            String[] splitString = rawString.split(";");
            switch (splitString[0]) {
                case "staffNotifications":
                    staffNotifications = boolFromString(splitString[1]);
                    break;
                case "punishmentAudit":
                    punishmentAudit = modeFromString(splitString[1]);
                    break;
                case "rankAudit":
                    rankAudit = modeFromString(splitString[1]);
                    break;
                case "sudoAudit":
                    sudoAudit = modeFromString(splitString[1]);
                    break;
                case "chatAudit":
                    chatAudit = modeFromString(splitString[1]);
                    break;
                case "basicAudits":
                    basicAudits = modeFromString(splitString[1]);
                    break;
                case "staffLeaveJoinNotification":
                    staffLeaveJoinNotification = modeFromString(splitString[1]);
                    break;
                case "anticheatLogs":
                    anticheatLogs = modeFromString(splitString[1]);
                    break;
                case "staffChat":
                    staffChat = modeFromString(splitString[1]);
                    break;
                case "reports":
                    reports = modeFromString(splitString[1]);
                    break;
            }
        }
    }

    private Mode modeFromString(String string) {
        switch (string) {
            case "NETWORK":
                return Mode.NETWORK;
            case "LOCAL":
                return Mode.LOCAL;
            case "DISABLED":
                return Mode.DISABLED;
            default:
                return null;
        }
    }

    protected Mode modeFromType(Notification.Type type) {
        switch (type) {
            case PUNISHMENT:
                return punishmentAudit;
            case RANK:
                return rankAudit;
            case SUDO:
                return sudoAudit;
            case CHAT:
                return chatAudit;
            case BASIC:
                return basicAudits;
            case LEAVEJOIN:
                return staffLeaveJoinNotification;
            case ACLOGS:
                return anticheatLogs;
            case STAFFCHAT:
                return staffChat;
            case REPORTS:
                return reports;
            default:
                return null;
        }
    }

    private boolean boolFromString(String string) {
        return string.equals("true");
    }
}
