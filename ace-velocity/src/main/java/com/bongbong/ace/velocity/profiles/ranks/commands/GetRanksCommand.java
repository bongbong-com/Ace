package com.bongbong.ace.velocity.profiles.ranks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.bongbong.ace.velocity.Locale;
import com.bongbong.ace.velocity.profiles.Profile;
import com.bongbong.ace.velocity.profiles.ranks.Rank;
import com.bongbong.ace.velocity.profiles.ranks.RankManager;
import com.bongbong.ace.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

@CommandAlias("getranks|listranks|ranks")
@RequiredArgsConstructor
public class GetRanksCommand extends BaseCommand {
    private final RankManager rankManager;
    private final Locale locale;

    @Default
    @CommandCompletion("@players")
    @Syntax("<player>")
    @CommandPermission("ranks.get")
    public void onDefault(CommandIssuer sender, Profile targetProfile) {
        if (targetProfile == null) {
            sender.sendMessage(Colors.translate(locale.TARGET_NOT_FOUND));
            return;
        }

        TreeMap<Integer, Rank> ranks = new TreeMap<>();
        for (Rank rank : rankManager.getAllRanks(targetProfile.getRanks()))
            ranks.put(rank.getWeight(), rank);


        if (ranks.isEmpty()) {
            sender.sendMessage(Colors.translate("&cThe target you specified does not have any ranks."));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&aRanks &7(" + ranks.size() + "&7)&f: ");
        List<Rank> list = new LinkedList<>(ranks.descendingMap().values());
        while (!list.isEmpty()) {
            Rank rank = list.get(0);
            list.remove(rank);
            sb.append(rank.getColor() + rank.getName());
            if (list.isEmpty()) sb.append("&f."); else sb.append("&f, ");
        }

        sender.sendMessage(Colors.translate(sb.toString()));
    }
}