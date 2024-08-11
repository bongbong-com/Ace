package com.bongbong.ace.velocity.profiles.ranks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.bongbong.ace.velocity.Locale;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.profiles.ranks.Rank;
import com.bongbong.ace.velocity.profiles.ranks.RankManager;
import com.bongbong.ace.velocity.utils.Colors;
import lombok.RequiredArgsConstructor;

import java.util.*;

@CommandAlias("rank|rm")
@RequiredArgsConstructor
@CommandPermission("ranks.manage")
public class RankCommand extends BaseCommand {
    private final RankManager rankManager;
    private final ProfileManager profileManager;
    private final Locale locale;

    @HelpCommand
    public void onHelp(CommandIssuer sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("list")
    @Description("lists all ranks in order.")
    public void onList(CommandIssuer sender) {
        Map<UUID, Rank> ranks = rankManager.getRanks();

        if (ranks.isEmpty()) {
            sender.sendMessage(Colors.translate("&cThere are no ranks, please create one with /rm create."));
            return;
        }

        StringBuilder sb = new StringBuilder();
        TreeMap<Integer, Rank> sortedRanks = new TreeMap<>();
        for (Rank rank : ranks.values()) sortedRanks.put(rank.getWeight(), rank);

        sb.append("&a&lRanks:");
        for (Rank rank : sortedRanks.descendingMap().values()) {
            sb.append("\n&f" + rank.getWeight() + " &7- &e" + rank.getDisplayName() + " &7(" + rank.getName() + ")");
            if (rank.isDefaultRank()) sb.append(" &6(default)");
        }

        sender.sendMessage(Colors.translate(sb.toString()));
    }

    @Subcommand("info")
    @Description("gets information on a rank.")
    @Syntax("<rank name>")
    @CommandCompletion("@ranks")
    public void onInfo(CommandIssuer sender, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&eName: &f" + rank.getName() + "\n&eDisplay Name: &f" + rank.getDisplayName()));
    }

    @Subcommand("create")
    @Description("creates a new rank.")
    @Syntax("<rank name> <weight>")
    @CommandCompletion("@ranks @range:1-100")
    public void onCreate(CommandIssuer sender, String rankName, int weight) {
        for (Rank rank : rankManager.getRanks().values()) {
            if (rank.getName().equalsIgnoreCase(rankName) || rank.getWeight() == weight) {
                sender.sendMessage(Colors.translate(
                        "&cThere is already a rank with the name or weight you specified."));
                return;
            }
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rankName + "&a with a weight of &f" + weight + "&a has been created."));
        rankManager.createRank(rankName, weight);
    }

    @Subcommand("delete|remove")
    @Description("deletes an existing rank.")
    @Syntax("<rank name>")
    @CommandCompletion("@ranks")
    public void onDelete(CommandIssuer sender, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        sender.sendMessage(Colors.translate("&aRank &f" + rank.getName() + "&a has been removed."));
        rankManager.remove(rank);

        profileManager.updateAllProfiles();
    }

    @Subcommand("rename")
    @Description("renames a rank.")
    @Syntax("<name> <new name>")
    @CommandCompletion("@ranks")
    public void onRename(CommandIssuer sender, Rank rank, String newName) {
        Rank newRank = rankManager.getRank(newName);

        if (rank == null || newRank != null) {
            sender.sendMessage(Colors.translate(
                    "&cOne of the rank names you specified is invalid or already exists."));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a has been renamed to &f" + newName + "&a."));
        rank.setName(newName);

        rankManager.push(rank);
    }

    @Subcommand("setprefix")
    @Description("sets a rank's prefix.")
    @Syntax("<rank name> <prefix>")
    @CommandCompletion("@ranks")
    public void onSetPrefix(CommandIssuer sender, String[] args) {
        Rank rank = rankManager.getRank(args[0]);

        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
            if (i + 1 != args.length) sb.append(" ");
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a now has the prefix: &r" + sb));
        rank.setPrefix(sb.toString());

        rankManager.push(rank);
    }

    @Subcommand("setcolor")
    @Description("sets a rank's color.")
    @Syntax("<rank name> <color>")
    @CommandCompletion("@ranks")
    public void onSetColor(CommandIssuer sender, Rank rank, String color) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a has a new color: &r" + color + "color"));
        rank.setColor(color);

        rankManager.push(rank);
    }

    @Subcommand("setdisplayname")
    @Description("sets a rank's display name.")
    @Syntax("<rank name> <display name>")
    @CommandCompletion("@ranks")
    public void onSetDisplayName(CommandIssuer sender, String[] args) {
        Rank rank = rankManager.getRank(args[0]);

        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
            if (i + 1 != args.length) sb.append(" ");
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a has a new display name: &f" + sb));
        rank.setDisplayName(sb.toString());

        rankManager.push(rank);
    }

    @Subcommand("setdefault")
    @Description("sets the rank that is given to new players.")
    @Syntax("<rank name>")
    @CommandCompletion("@ranks")
    public void onSetDefault(CommandIssuer sender, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        if (rank.isDefaultRank()) {
            sender.sendMessage(Colors.translate(
                    "&cThe rank you specified is already the default rank."));
            return;
        }

        Rank defaultRank = rankManager.getDefaultRank();
        if (defaultRank != null) {
            defaultRank.setDefaultRank(false);
            sender.sendMessage(
                    "&aRank &f" + rank.getName() + " &ais now the default rank instead of &f" + defaultRank.getName() + "&a.");
            rankManager.push(defaultRank);
        } else {
            sender.sendMessage(Colors.translate(
                    "&aRank &f" + rank.getName() + " &ais now the default rank."));
        }

        rank.setDefaultRank(true);
        rankManager.push(rank);
    }

    @Subcommand("addparent")
    @Description("adds a parent to a rank.")
    @Syntax("<rank name> <parent name>")
    @CommandCompletion("@ranks @ranks")
    public void onAddParent(CommandIssuer sender, Rank rank, Rank parent) {
        if (rank == null || parent == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        if (rank.getParents().contains(parent.getUuid())) {
            sender.sendMessage(Colors.translate(
                    "&cRank &f" + rank.getName() + "&c already has this parent."));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a has a new parent rank:&f " + parent.getName()));
        rank.getParents().add(parent.getUuid());

        rankManager.push(rank);
    }

    @Subcommand("removeparent|deleteparent")
    @Description("removes a parent from a rank.")
    @Syntax("<rank name> <parent name>")
    @CommandCompletion("@ranks @ranks")
    public void onRemoveParent(CommandIssuer sender, Rank rank, Rank parent) {
        if (rank == null || parent == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        if (!rank.getParents().contains(parent.getUuid())) {
            sender.sendMessage(Colors.translate(
                    "&cRank &f" + rank.getName() + "&c doesn't have this parent."));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a no longer has this parent rank: &f" + parent.getName()));
        rank.getParents().remove(parent.getUuid());

        rankManager.push(rank);
    }

    @Subcommand("permissions|perms")
    @Description("list a rank's permissions.")
    @Syntax("<rank name>")
    @CommandCompletion("@ranks")
    public void onPermissions(CommandIssuer sender, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        List<String> perms = rank.getPermissions();

        if (perms == null || perms.isEmpty()) {
            sender.sendMessage(Colors.translate("&cNo permissions set for that rank!"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("&aPermissions &7(" + perms.size() + ")&a:");

        for (String perm : perms) sb.append(" &e" + perm);

        sender.sendMessage(Colors.translate(sb.toString()));
    }

    @Subcommand("addpermission|addperm")
    @Description("adds a permission to a rank.")
    @Syntax("<rank name> <permission>")
    @CommandCompletion("@ranks")
    public void onAddPermission(CommandIssuer sender, Rank rank, String permission) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        List<String> perms = rank.getPermissions();
        if (perms == null) perms = new ArrayList<>();

        if (perms.contains(permission)) {
            sender.sendMessage(Colors.translate("&cThat permission has already been set."));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a now has the permission &f" + permission + "&a."));
        perms.add(permission);

        rankManager.push(rank);
        profileManager.updateAllProfiles();
    }

    @Subcommand("removepermission|removeperm|delperm")
    @Description("removes a permission from a rank.")
    @Syntax("<rank name> <permission>")
    @CommandCompletion("@ranks")
    public void onRemovePermission(CommandIssuer sender, Rank rank, String permission) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        List<String> perms = rank.getPermissions();
        if (perms == null) perms = new ArrayList<>();

        if (!perms.contains(permission)) {
            sender.sendMessage(Colors.translate("&cThat permission is not set."));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a no longer has the permission &f" + permission + "&a."));
        perms.remove(permission);

        rankManager.push(rank);
        profileManager.updateAllProfiles();
    }

    @Subcommand("adddescription")
    @Description("adds a line of description in chat hover.")
    @Syntax("<rank name> <description line>")
    @CommandCompletion("@ranks")
    public void onAddDescription(CommandIssuer sender, String[] args) {
        Rank rank = rankManager.getRank(args[0]);

        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]);
            if (i + 1 != args.length) sb.append(" ");
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a now has description line: &r" + sb));
        rank.getDescriptionLines().add(sb.toString());

        rankManager.push(rank);
    }

    @Subcommand("removedescription")
    @Description("delete last line from description.")
    @Syntax("<rank name>")
    @CommandCompletion("@ranks")
    public void onRemoveDescription(CommandIssuer sender, Rank rank) {
        if (rank == null) {
            sender.sendMessage(Colors.translate(locale.RANK_NOT_FOUND));
            return;
        }

        List<String> descriptionLines = rank.getDescriptionLines();

        int i = descriptionLines.size() - 1;
        if (i < 0) {
            sender.sendMessage(Colors.translate("&cNo lines of description to remove."));
            return;
        }

        sender.sendMessage(Colors.translate(
                "&aRank &f" + rank.getName() + "&a deleted a description line"));
        descriptionLines.remove(i);

        rankManager.push(rank);
    }
}