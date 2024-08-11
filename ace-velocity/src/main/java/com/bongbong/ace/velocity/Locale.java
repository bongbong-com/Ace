package com.bongbong.ace.velocity;

import com.bongbong.ace.velocity.utils.YamlConfig;

public class Locale extends YamlConfig {
    @Comment({
            " ",
            "Configure the messages for the plugin",
            " "
    })

    public String NO_PERMISSION = "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.";
    public String TARGET_NOT_FOUND = "&cThe target player has never logged into the server.";
    public String ONLY_PLAYERS = "&cOnly players can use this command!";
    public String TARGET_NOT_ONLINE = "&cThe target player is not currently online.";
    public String RANK_NOT_FOUND = "&cThe rank(s) you specified do not exist.";
}
