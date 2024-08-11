package com.bongbong.ace.velocity.profiles.ranks;

import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.velocity.Locale;
import com.bongbong.ace.velocity.database.Mongo;
import com.bongbong.ace.velocity.database.MongoUpdate;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.profiles.ranks.commands.AddRankCommand;
import com.bongbong.ace.velocity.profiles.ranks.commands.GetRanksCommand;
import com.bongbong.ace.velocity.profiles.ranks.commands.RankCommand;
import com.bongbong.ace.velocity.profiles.ranks.commands.RemoveRankCommand;
import com.bongbong.ace.velocity.utils.Registrar;
import com.bongbong.ace.velocity.utils.TaskScheduler;
import com.mongodb.client.FindIterable;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

public class RankManager {
    private final Mongo mongo;
    private final @Getter Map<UUID, Rank> ranks;

    public RankManager(Mongo mongo, VelocityCommandManager commandManager,
                       ProfileManager profileManager, Locale locale,
                       Registrar registrar, TaskScheduler scheduler) {

        this.mongo = mongo;
        this.ranks = new HashMap<>();

        mongo.createCollection("ranks");
        FindIterable<Document> iterable = mongo.getCollectionIterable("ranks");
        iterable.forEach(document -> pull(document.get("_id", UUID.class)));

        commandManager.getCommandContexts().registerContext(Rank.class, (c) -> getRank(c.popFirstArg()));
        commandManager.getCommandCompletions().registerCompletion("ranks", (c) -> {
            Collection<String> names = new ArrayList<>();
            getRanks().values().forEach(rank -> names.add(rank.getName()));

            return names;
        });

        commandManager.registerCommand(new RankCommand(this, profileManager, locale));
        commandManager.registerCommand(new AddRankCommand(profileManager, locale));
        commandManager.registerCommand(new RemoveRankCommand(profileManager, locale));
        commandManager.registerCommand(new GetRanksCommand(this, locale));

        registrar.registerListener(new RankListener(profileManager, this, scheduler));
    }

    public Rank createRank(String name, int weight) {
        Rank rank = new Rank(UUID.randomUUID());

        rank.setName(name.toLowerCase());
        rank.setDisplayName(name);
        rank.setWeight(weight);
        ranks.put(rank.getUuid(), rank);

        push(rank);

        return rank;
    }

    public Rank getRank(UUID uuid) {
        return ranks.get(uuid);
    }

    public Rank getRank(String name) {
        for (Rank rank : ranks.values())
            if (rank.getName().equalsIgnoreCase(name)) return rank;

        return null;
    }

    public Rank getDefaultRank() {
        for (Rank rank : ranks.values())
            if (rank.isDefaultRank()) return rank;

        return null;
    }

    public Rank pull(UUID uuid) {
        Document document = mongo.getDocument("ranks", "_id", uuid);

        if (document == null) return null;

        Rank rank = new Rank(uuid);
        rank.importFromDocument(document);
        ranks.put(uuid, rank);

        return rank;
    }

    public void push(Rank rank) {
        MongoUpdate mu = new MongoUpdate("ranks", rank.getUuid());
        mu.setUpdate(rank.exportToDocument());

        mongo.massUpdate(mu);
    }

    public void remove(Rank rank) {
        mongo.deleteDocument("ranks", "_id", rank.getUuid());
        ranks.remove(rank.getUuid());
    }

    public List<String> getParentPermissions(Rank rank) {
        List<String> perms = new ArrayList<>();

        for (UUID uuid : rank.getParents()) {
            Rank pRank = getRank(uuid);
            perms.addAll(pRank.getPermissions());

            if (!pRank.getParents().isEmpty()) perms.addAll(getParentPermissions(pRank));
        }

        return perms;
    }

    public List<String> getAllPermissions(Rank rank) {
        List<String> perms = new ArrayList<>(rank.getPermissions());
        perms.addAll(getParentPermissions(rank));

        return perms;
    }

    public List<Rank> getAllRanks(List<UUID> rankIds) {
        List<Rank> ranks = new ArrayList<>();
        for (UUID uuid : rankIds) ranks.add(getRank(uuid));
        return ranks;
    }

    public Rank getHighestRank(List<Rank> ranks) {
        Rank rank = null;
        for (Rank r : ranks) {
            if (rank == null) {
                rank = r;
                continue;
            }

            if (r.getWeight() > rank.getWeight()) rank = r;
        }

        return rank;
    }

    public Rank getHighestRankFromId(List<UUID> rankIds) {
        return getHighestRank(getAllRanks(rankIds));
    }
}
