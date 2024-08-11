package com.bongbong.ace.velocity.profiles.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import com.bongbong.ace.velocity.profiles.Profile;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.profiles.ranks.Rank;
import com.bongbong.ace.velocity.profiles.ranks.RankManager;
import com.bongbong.ace.velocity.utils.Colors;
import com.bongbong.ace.velocity.utils.PlayerFinder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// TODO: make sure this is optimized and doesn't use too much cpu when ran
// if it does, make sure to add cooldown or something idk
@CommandAlias("list|ls")
@RequiredArgsConstructor
public class ListCommand extends BaseCommand {
    private final PlayerFinder playerFinder;
    private final ProfileManager profileManager;
    private final RankManager rankManager;

    @Default
    @Syntax("[-legacy|-l] [-global|-g]")
    public void execute(CommandIssuer issuer, @Optional String[] args) {
        ServerConnection server = null;
        if (issuer.isPlayer()) {
            Player player = playerFinder.getPlayer(issuer.getUniqueId());
            if (!player.getCurrentServer().isPresent()) server = player.getCurrentServer().get();
        }

        StringBuilder sb = new StringBuilder();
        boolean legacy = false;
        boolean global = false;
        if (args != null) for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.equalsIgnoreCase("-legacy") || s.equalsIgnoreCase("-l")) legacy = true;
            else if (s.equalsIgnoreCase("-global") || s.equalsIgnoreCase("-g")) global = true;
            else {
                sb.append(args[i]);
                if (i + 1 != args.length) sb.append(" ");
            }
        }

        Map<Component, Integer> players = new HashMap<>();

        if (server == null) global = true;
        if (!issuer.isPlayer()) legacy = true;

        if (global) for (Profile profile : profileManager.getProfiles().values()) {
            Rank rank = rankManager.getHighestRankFromId(profile.getRanks());
            if (legacy) players.put(Colors.get(rank.getPrefix() + " " + rank.getColor() + profile.getName()), rank.getWeight());
            else players.put(Colors.get(rank.getColor() + profile.getName()).hoverEvent(HoverEvent.hoverEvent(
                    HoverEvent.Action.SHOW_TEXT, Colors.get(rank.getPrefix() + ""))), rank.getWeight());
        } else for (Player player : server.getServer().getPlayersConnected()) {
            Profile profile = profileManager.getProfile(player.getUniqueId());
            Rank rank = rankManager.getHighestRankFromId(profile.getRanks());
            if (legacy) players.put(Colors.get(rank.getPrefix() + " " + rank.getColor() + profile.getName()), rank.getWeight());
            else players.put(Colors.get(rank.getColor() + profile.getName()).hoverEvent(HoverEvent.hoverEvent(
                    HoverEvent.Action.SHOW_TEXT, Colors.get(rank.getPrefix() + ""))), rank.getWeight());
        }

        List<Component> names = players.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        JoinConfiguration configuration = JoinConfiguration.builder().separator(Colors.get("&f, ")).build();

        Component component = Colors.get("&fOnline Players (" + names.size() + "/1000" + "): ")
                .append(Component.join(configuration, names));

        if (!issuer.isPlayer()) issuer.sendMessage(Colors.convertToString(component));
        else playerFinder.getPlayer(issuer.getUniqueId()).sendMessage(component);
    }
}
