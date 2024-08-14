package com.bongbong.ace.velocity;


import co.aikar.commands.VelocityCommandManager;
import com.bongbong.ace.shared.redis.RedisManager;
import com.bongbong.ace.velocity.database.Mongo;
import com.bongbong.ace.velocity.network.NetworkManager;
import com.bongbong.ace.velocity.profiles.ProfileManager;
import com.bongbong.ace.velocity.staff.StaffManager;
import com.bongbong.ace.velocity.utils.*;
import com.google.inject.Inject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.cirrus.velocity.CirrusVelocity;
import dev.simplix.cirrus.velocity.plugin.CirrusTestCommand;
import net.elytrium.limboapi.api.LimboFactory;
import org.bson.UuidRepresentation;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "ace",
        name = "Ace",
        version = "1.0-SNAPSHOT",
        description = "Network core (Profiles, Ranks, Punishments, etc)",
        url = "www.bongbong.com",
        authors = "tigerbong",
        dependencies = {
                @Dependency(id = "protocolize"),
                @Dependency(id = "limboapi"),
        }
)
public class AcePlugin {
    private final ProxyServer server;
    private final Logger logger;
    @Inject @DataDirectory private Path dataDirectory;

    @Inject
    public AcePlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.reload(dataDirectory.resolve("database.yml").toFile());

        PunishmentSettings settings = new PunishmentSettings();
        settings.reload(dataDirectory.resolve("punishments.yml").toFile());

        Locale locale = new Locale();
        locale.reload(dataDirectory.resolve("messages.yml").toFile());

        MongoClientSettings mongoSettings;
        if (databaseConfig.LOCAL_MONGO) mongoSettings = MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder -> builder
                        .minSize(50)
                        .maxSize(100))
                .uuidRepresentation(UuidRepresentation.STANDARD).build();
        else mongoSettings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyToConnectionPoolSettings(builder -> builder
                        .minSize(50)
                        .maxSize(100))
                .applyConnectionString(new ConnectionString(databaseConfig.MONGO_URI))
                .build();

        Mongo mongo = new Mongo(MongoClients.create(mongoSettings).getDatabase(databaseConfig.MONGO_DATABASE));
        RedisManager redisManager = new RedisManager(databaseConfig.REDIS_CHANNEL, databaseConfig.REDIS_ADDRESS, databaseConfig.REDIS_PORT, null, null);

        TaskScheduler taskScheduler = new TaskScheduler(this, server.getScheduler());
        PlayerFinder playerFinder = new PlayerFinder(server);
        Registrar registrar = new Registrar(server.getEventManager(), this);
        ServerBroadcaster broadcaster = new ServerBroadcaster(server);

        VelocityCommandManager commandManager = new VelocityCommandManager(server, this);
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandContexts().registerContext(OfflinePlayer.class, (c) -> {
            String string = c.popFirstArg();

            Player player = playerFinder.getPlayer(string);
            OfflinePlayer offlinePlayer = new OfflinePlayer();
            if (player != null) {
                offlinePlayer.setUniqueId(player.getUniqueId());
                offlinePlayer.setUsername(player.getUsername());

                return offlinePlayer;
            }

            c.getSender().sendMessage(Colors.get("&aQuerying database for offline player..."));
            WebPlayer webPlayer = new WebPlayer(string);

            if (!webPlayer.isValid()) {
                c.getSender().sendMessage(Colors.get("&cNo player with that name exists (Mojang API)."));
                return null;
            }

            offlinePlayer.setUniqueId(webPlayer.getUuid());
            offlinePlayer.setUsername(webPlayer.getName());
            return offlinePlayer;
        });

        LimboFactory limboFactory = (LimboFactory) server.getPluginManager()
                .getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();

        new CirrusVelocity(this, server, server.getCommandManager()).init();
        server.getCommandManager().register("menu", new CirrusTestCommand());

        new ProfileManager(
                taskScheduler, mongo, commandManager, playerFinder, locale, redisManager, registrar,
                logger);
        new StaffManager(
                mongo, playerFinder, commandManager, redisManager, registrar, broadcaster, settings,
                logger);
        new NetworkManager(
                limboFactory, dataDirectory, server.getServer("test").get(), commandManager, playerFinder
        );
    }
}
