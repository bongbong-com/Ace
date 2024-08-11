package com.bongbong.ace.velocity;

import com.bongbong.ace.velocity.utils.YamlConfig;

public class PunishmentSettings extends YamlConfig {
    @Comment({
            " ",
            "too lazy to make this config in a 'nice' way, dont ask me to change format",
            "you can add as many things as you want, make sure each list has equal index",
            " ",
            "keywords prioritized via index. if spam & cheat are both in the reason, cheat will take priority since its lower index",
            " "
    })

    public  String REASONS = "cheating;badchat;whatever";
    public String KEYWORDS = "cheat,hack,fly,bhop,killaura;spam,lowquality;wtf";
    public String PUNISHMENTS = "ban:30d,ban:90d,ban:365d;mute:1h,mute:1d,mute:3d,mute:7d;ban:1m,ban:2m";
    public String MESSAGE = "Using any client side modification that gives an unfair\nadvantage over vanilla players is strictly prohibited.;" +
            "Refrain from posting low quality or otherwise spammy\nchat messages that can annoy other players;why tf";

}