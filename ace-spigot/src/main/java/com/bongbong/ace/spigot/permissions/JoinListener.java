package com.bongbong.ace.spigot.permissions;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class JoinListener implements Listener {
    final Plugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PermissionInjector.inject(plugin, event.getPlayer(), new CorePermissibleBase(event.getPlayer()));
    }
}
