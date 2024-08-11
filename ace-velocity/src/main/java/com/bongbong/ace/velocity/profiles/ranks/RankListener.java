package com.bongbong.ace.velocity.profiles.ranks;

import com.bongbong.ace.velocity.profiles.Profile;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.utils.TaskScheduler;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class RankListener {
    final ProfileManager profileManager;
    final RankManager rankManager;
    final TaskScheduler scheduler;

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerPermissionsSetup(PermissionsSetupEvent event, Continuation continuation) {
        if (!(event.getSubject() instanceof Player)) {
            continuation.resume();
            return;
        }

        Player player = (Player) event.getSubject();
        UUID uuid = player.getUniqueId();

        if (profileManager.isProfileCached(uuid)) {
            Profile profile = profileManager.getProfile(uuid);
            if (profile == null) profileManager.removeProfile(uuid);
            else profileManager.pushProfile(profile, true);
        }

        if (profileManager.pullProfile(uuid, true) == null) profileManager.createProfile(uuid);

        Profile profile = profileManager.getProfile(player.getUniqueId());
        if (profile == null) System.out.println("ahhhhh!");

        event.setProvider(new AcePermissionProvider(profile, player, rankManager));

        continuation.resume();
    }

    @Subscribe(order = PostOrder.NORMAL)
    public void onPlayerJoinServer(ServerConnectedEvent event) {
        Profile profile = profileManager.getProfile(event.getPlayer().getUniqueId());
        profileManager.updateProfile(profile);
    }
}
