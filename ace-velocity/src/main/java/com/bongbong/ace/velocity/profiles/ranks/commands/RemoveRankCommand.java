package com.bongbong.ace.velocity.profiles.ranks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.bongbong.ace.velocity.Locale;
import com.bongbong.ace.velocity.profiles.Profile;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.profiles.ranks.Rank;
import com.bongbong.ace.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;

@CommandAlias("removerank|remrank")
@RequiredArgsConstructor
public class RemoveRankCommand extends BaseCommand {
    private final ProfileManager profileManager;
    private final Locale locale;

    @Default
    @CommandCompletion("@players @ranks")
    @Syntax("<player> <rank name>")
    @CommandPermission("ranks.remove")
    public void onDefault(CommandIssuer sender, Profile targetProfile, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        if (targetProfile == null) {
            sender.sendMessage(Colors.translate(locale.TARGET_NOT_FOUND));
            return;
        }

        if (!targetProfile.getRanks().contains(rank.getUuid())) {
            sender.sendMessage(Colors.translate("&cThe target you specified does not have that rank."));
            return;
        }

        targetProfile.getRanks().remove(rank.getUuid());

        sender.sendMessage(Colors.translate(
                rank.getColor() + targetProfile.getName() + "&a no longer has the rank " + rank.getColor() + rank.getName() + "&a."));

        profileManager.pushProfile(targetProfile, false);
        profileManager.updateProfile(targetProfile);
    }
}
