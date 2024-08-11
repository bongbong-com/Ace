package com.bongbong.ace.velocity.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ServerBroadcaster {
    final ProxyServer proxyServer;

    public void broadcast(String string, String server) {
        Optional<RegisteredServer> registeredServer = proxyServer.getServer(server).isPresent() ?
                proxyServer.getServer(server) : Optional.empty();

        if (!registeredServer.isPresent()) return;

        registeredServer.get().getPlayersConnected().forEach(player -> player.sendMessage(Colors.get(string)));
    }

    public void broadcast(String string) {
        proxyServer.getAllServers().forEach(server ->
                server.getPlayersConnected().forEach(player -> player.sendMessage(Colors.get(string))));
    }
}
