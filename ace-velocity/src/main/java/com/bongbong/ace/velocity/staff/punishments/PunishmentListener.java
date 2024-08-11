package com.bongbong.ace.velocity.staff.punishments;

import com.bongbong.ace.velocity.PunishmentSettings;
import com.bongbong.ace.velocity.utils.Colors;
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
public class PunishmentListener {
    private final PunishmentManager punishmentManager;
    private final PunishmentSettings settings;

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (punishmentManager.isProfileCached(uuid)) {
            event.setResult(ResultedEvent.ComponentResult.denied(
                    Component.text("Your profile is already loaded, please relog! (Err Code: ACE3)")));

            PunishmentProfile punishmentProfile = punishmentManager.getProfile(uuid);

            if (punishmentProfile == null) punishmentManager.removeProfile(uuid);
            else punishmentManager.pushProfile(punishmentProfile, true);

            return;
        }

        PunishmentProfile punishmentProfile = punishmentManager.pullProfile(uuid, true);
        if (punishmentProfile == null) punishmentProfile = punishmentManager.createProfile(uuid);

        punishmentProfile.addIp(event.getPlayer().getRemoteAddress().getAddress().getHostAddress());

        Punishment blacklist = punishmentManager.getActivePunishment(punishmentProfile, Punishment.Type.BLACKLIST);
        Punishment ban = punishmentManager.getActivePunishment(punishmentProfile, Punishment.Type.BAN);

        if (blacklist != null) event.setResult(ResultedEvent.ComponentResult.denied(
                Colors.get(String.join("\n", blacklist.getMessage(settings)))));

        if (ban != null) event.setResult(ResultedEvent.ComponentResult.denied(
                Colors.get(String.join("\n", ban.getMessage(settings)))));

        Punishment mute = punishmentManager.getActivePunishment(punishmentProfile, Punishment.Type.MUTE);
        if (mute != null) punishmentProfile.setMuteExpiration(mute.getExpires());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onLastLogin(LoginEvent event) {
        if (event.getResult().isAllowed()) return;

        PunishmentProfile punishmentProfile = punishmentManager.getProfile(event.getPlayer().getUniqueId());
        punishmentManager.pushProfile(punishmentProfile, true);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        PunishmentProfile punishmentProfile = punishmentManager.getProfile(player.getUniqueId());

        if (punishmentProfile == null) return;

        punishmentManager.pushProfile(punishmentProfile, true);
    }
}

