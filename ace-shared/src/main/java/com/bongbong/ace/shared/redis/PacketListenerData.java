package com.bongbong.ace.shared.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
public class PacketListenerData {

    private Object instance;
    private Method method;
    private Class packetClass;

    public boolean matches(Packet packet) {
        return this.packetClass == packet.getClass();
    }

}