package com.bongbong.ace.velocity.profiles;

import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public @Data class Settings {
    public enum Mode {
        EVERYONE,
        FRIENDS,
        DISABLED
    }

    private Mode globalChat = Mode.EVERYONE, privateMessages = Mode.EVERYONE;
    private boolean scoreBoard = true, sounds = true, friendRequests = true, staffChat = false, staffMessages = true;

    protected List<String> export() {
        List<String> list = new ArrayList<>();

        list.add("globalChat;" + globalChat.toString());
        list.add("privateMessages;" + privateMessages.toString());
        list.add("scoreBoard;" + scoreBoard);
        list.add("sounds;" + sounds);
        list.add("friendRequests;" + friendRequests);
        list.add("staffChat;" + staffChat);
        list.add("staffMessages;" + staffMessages);

        return list;
    }

    protected void importFromDocument(Document document) {
        List<String> rawSettings = document.getList("settings", String.class);
        for (String rawString : rawSettings) {
            String[] splitString = rawString.split(";");
            switch (splitString[0]) {
                case "globalChat":
                    globalChat = modeFromString(splitString[1]);
                    break;
                case "privateMessages":
                    privateMessages = modeFromString(splitString[1]);
                    break;
                case "scoreBoard":
                    scoreBoard = boolFromString(splitString[1]);
                    break;
                case "sounds":
                    sounds = boolFromString(splitString[1]);
                    break;
                case "friendRequests":
                    friendRequests = boolFromString(splitString[1]);
                    break;
                case "staffChat":
                    staffChat = boolFromString(splitString[1]);
                    break;
                case "staffMessages":
                    staffMessages = boolFromString(splitString[1]);
                    break;
            }
        }
    }

    private Mode modeFromString(String string) {
        switch (string) {
            case "EVERYONE":
                return Mode.EVERYONE;
            case "FRIENDS":
                return Mode.FRIENDS;
            case "NOBODY":
                return Mode.DISABLED;
            default:
                return null;
        }
    }

    private boolean boolFromString(String string) {
        return string.equals("true");
    }
}
