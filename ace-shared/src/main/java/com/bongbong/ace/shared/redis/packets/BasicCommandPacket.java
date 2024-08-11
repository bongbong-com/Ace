package com.bongbong.ace.shared.redis.packets;

import com.bongbong.ace.shared.redis.Packet;
import com.bongbong.ace.shared.utils.JsonChain;
import com.google.gson.JsonObject;
import lombok.Getter;

public class BasicCommandPacket implements Packet {

    @Getter private String senderName, orginServer, message;

    public BasicCommandPacket() {} //for init of class type

    public BasicCommandPacket(String orginServer, String senderName, String message) {
        this.orginServer = orginServer;
        this.senderName = senderName;
        this.message = message;
    }

    @Override
    public int id() {
        return 2;
    }

    @Override
    public JsonObject serialize() {
        return new JsonChain()
                .addProperty("orginServer", orginServer)
                .addProperty("senderUuid", senderName)
                .addProperty("message", message)
                .get();
    }

    @Override
    public void deserialize(JsonObject object) {
        orginServer = object.get("orginServer").getAsString();
        senderName = object.get("senderUuid").getAsString();
        message = object.get("message").getAsString();
    }
}

