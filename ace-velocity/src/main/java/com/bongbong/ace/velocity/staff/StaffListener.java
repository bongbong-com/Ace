package com.bongbong.ace.velocity.staff;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@RequiredArgsConstructor
public class StaffListener {
    private final StaffManager staffManager;

    @Subscribe(order = PostOrder.LATE)
    public void onFirstLogin(LoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (staffManager.isProfileCached(uuid)) {
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("Your profile is already loaded, please relog! (Err Code: ACE7)")));

            StaffProfile profile = staffManager.getProfile(uuid);

            if (profile == null) staffManager.removeProfile(uuid);
            else staffManager.pushProfile(profile, true);

            return;
        }

        if (!player.hasPermission("network.staff")) return;
        if (staffManager.pullProfile(uuid, true) == null) staffManager.createProfile(uuid);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onLastLogin(LoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("network.staff")) return;

        StaffProfile profile = staffManager.getProfile(uuid);
        if (profile == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("Your profile did not load properly, please relog! (Err Code: ACE8)")));
            return;
        }

        if (!event.getResult().isAllowed()) staffManager.pushProfile(profile, true);
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("network.staff")) return;

        StaffProfile profile = staffManager.getProfile(player.getUniqueId());

        if (profile == null) return;

        staffManager.pushProfile(profile, true);
    }

}
