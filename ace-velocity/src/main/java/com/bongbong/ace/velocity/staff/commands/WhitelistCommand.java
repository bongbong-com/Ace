package com.bongbong.ace.velocity.staff.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.bongbong.ace.velocity.Locale;
import com.bongbong.ace.velocity.profiles.Profile;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.profiles.ranks.Rank;
import com.bongbong.ace.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;

@CommandAlias("whitelist")
@RequiredArgsConstructor
public class WhitelistCommand extends BaseCommand {
    private final ProfileManager profileManager;
    private final Locale locale;

    @Default
    @CommandCompletion("@whitelist")
    @Syntax("<type>")
    @CommandPermission("ranks.add")
    public void onDefault(CommandIssuer sender, Profile targetProfile, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        if (targetProfile == null) {
            sender.sendMessage(Colors.translate(locale.TARGET_NOT_FOUND));
            return;
        }

        if (targetProfile.getRanks().contains(rank.getUuid())) {
            sender.sendMessage(Colors.translate("&cThe target you specified already has that rank."));
            return;
        }

        targetProfile.getRanks().add(rank.getUuid());

        sender.sendMessage(Colors.translate(
                rank.getColor() + targetProfile.getName() + "&a now has the rank " + rank.getColor() + rank.getName() + "&a."));

        profileManager.pushProfile(targetProfile, false);
        profileManager.updateProfile(targetProfile);
    }
}