package com.bongbong.ace.velocity.utils;

import com.velocitypowered.api.event.EventManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Registrar {
    final EventManager eventManager;
    final Object pluginObject;

    public void registerListener(Object listener) {
        eventManager.register(pluginObject, listener);
    }
}
