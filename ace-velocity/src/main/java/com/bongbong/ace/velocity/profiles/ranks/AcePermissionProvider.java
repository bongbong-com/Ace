package com.bongbong.ace.velocity.profiles.ranks;

import com.bongbong.ace.velocity.profiles.Profile;
import com.google.common.base.Preconditions;
import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.PermissionProvider;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.Player;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AcePermissionProvider implements PermissionProvider, PermissionFunction {
    private final Profile profile;
    private final Player player;
    private final RankManager rankManager;

    @Override
    public Tristate getPermissionValue(String string) {
        Rank highestRank = rankManager.getHighestRankFromId(profile.getRanks());
        if (highestRank == null) return Tristate.FALSE;

        List<String> permissions = rankManager.getAllPermissions(highestRank);
        return permissions.contains(string) ? Tristate.TRUE : Tristate.FALSE;
    }

    @Override
    public @NonNull PermissionFunction createFunction(@NonNull PermissionSubject subject) {
        Preconditions.checkState(subject == this.player, "createFunction called with different argument");
        return this;
    }
}