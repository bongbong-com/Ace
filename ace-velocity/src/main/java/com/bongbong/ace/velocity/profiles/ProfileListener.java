package com.bongbong.ace.velocity.profiles;

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
public class ProfileListener {
    private final ProfileManager profileManager;

    @Subscribe(order = PostOrder.EARLY)
    public void onFirstLogin(LoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (profileManager.isProfileCached(uuid)) {
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("Your profile is already loaded, please relog! (Err Code: ACE1)")));

            Profile profile = profileManager.getProfile(uuid);

            if (profile == null) profileManager.removeProfile(uuid);
            else profileManager.pushProfile(profile, true);

            return;
        }

        if (profileManager.pullProfile(uuid, true) == null) profileManager.createProfile(uuid);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onLastLogin(LoginEvent event) {
        Profile profile = profileManager.getProfile(event.getPlayer().getUniqueId());
        if (profile == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("Your profile did not load properly, please relog! (Err Code: ACE2)")));
            return;
        }

        profile.setName(event.getPlayer().getUsername());

        if (!event.getResult().isAllowed()) profileManager.pushProfile(profile, true);
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        if (profile == null) return;

        profileManager.pushProfile(profile, true);
    }

}
