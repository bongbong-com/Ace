package com.bongbong.ace.spigot;

import co.aikar.commands.PaperCommandManager;
import com.bongbong.ace.shared.redis.RedisManager;
import com.bongbong.ace.spigot.commands.KillCommand;
import com.bongbong.ace.spigot.permissions.JoinListener;
import com.bongbong.ace.spigot.permissions.PermissionPacketListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class AcePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        RedisManager redisManager = new RedisManager(getConfig().getString("REDIS.CHANNEL"), getConfig().getString("REDIS.HOST"),
                getConfig().getInt("REDIS.PORT"), null, null);

        String serverName = getConfig().getString("SERVER_NAME");
        if (serverName.equals("properties")) serverName = getServer().getName();

        // Permissions stuff
        registerListener(new JoinListener(this));
        redisManager.registerListener(new PermissionPacketListener(this));

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new KillCommand(redisManager, serverName));
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

}
