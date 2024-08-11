package com.bongbong.ace.velocity;

import com.bongbong.ace.velocity.utils.YamlConfig;

public class DatabaseConfig extends YamlConfig {
    @Comment({
            " ",
            "Don't touch unless you know what you're doing!",
            "Database connection details and other critical things",
            " "
    })

    @Comment("If the mongo is local and not using authentication use this (will ignore MONGO_URI)")
    public boolean LOCAL_MONGO = true;
    public String MONGO_DATABASE = "ace";
    @Comment("ignored if LOCAL_MONGO is true")
    public String MONGO_URI = "mongodb://user:pass@127.0.0.1/ace";
    public String REDIS_CHANNEL = "ace";
    public String REDIS_ADDRESS = "127.0.0.1";
    public int REDIS_PORT = 6379;
}