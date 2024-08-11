package com.bongbong.ace.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerFinder {
    final ProxyServer proxyServer;

    public Player getPlayer(UUID uuid) {
        return proxyServer.getPlayer(uuid).isPresent() ? proxyServer.getPlayer(uuid).get() : null;
    }

    public Collection<Player> getPlayersAtServer(String server) {
        return proxyServer.getServer(server).isPresent() ? proxyServer.getServer(server).get().getPlayersConnected() : null;
    }

    public Collection<Player> getNetworkPlayers() {
        return proxyServer.getAllPlayers();
    }

    public Player getPlayer(String username) {
        return proxyServer.getPlayer(username).isPresent() ? proxyServer.getPlayer(username).get() : null;
    }

    public String getPlayerName(UUID uuid) {
        Optional<Player> player = proxyServer.getPlayer(uuid);
        if (player.isPresent()) return player.get().getUsername();

        return new WebPlayer(uuid).getName();
    }

    public UUID getPlayerUUID(String username) {
        Optional<Player> player = proxyServer.getPlayer(username);
        if (player.isPresent()) return player.get().getUniqueId();

        return new WebPlayer(username).getUuid();
    }

    public OfflinePlayer getOPlayerFromUUID(UUID uuid) {
        Player player = getPlayer(uuid);
        OfflinePlayer offlinePlayer = new OfflinePlayer();
        if (player != null) {
            offlinePlayer.setUniqueId(player.getUniqueId());
            offlinePlayer.setUsername(player.getUsername());

            return offlinePlayer;
        }

        WebPlayer webPlayer = new WebPlayer(uuid);

        if (!webPlayer.isValid()) return null;

        offlinePlayer.setUniqueId(webPlayer.getUuid());
        offlinePlayer.setUsername(webPlayer.getName());
        return offlinePlayer;
    }

    public OfflinePlayer getOPlayerFromUsername(String username) {
        Player player = getPlayer(username);
        OfflinePlayer offlinePlayer = new OfflinePlayer();
        if (player != null) {
            offlinePlayer.setUniqueId(player.getUniqueId());
            offlinePlayer.setUsername(player.getUsername());

            return offlinePlayer;
        }

        WebPlayer webPlayer = new WebPlayer(username);

        if (!webPlayer.isValid()) return null;

        offlinePlayer.setUniqueId(webPlayer.getUuid());
        offlinePlayer.setUsername(webPlayer.getName());
        return offlinePlayer;
    }
}
