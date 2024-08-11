package com.bongbong.ace.shared.redis.packets;

import com.bongbong.ace.shared.redis.Packet;
import com.bongbong.ace.shared.utils.JsonChain;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UpdatePermissionsPacket implements Packet {

    @Getter private UUID targetUuid;
    @Getter private List<String> permissions;
    @Getter private String color, prefix;

    public UpdatePermissionsPacket() {} //for init of class type

    public UpdatePermissionsPacket(UUID targetUuid, List<String> permissions, String color, String prefix) {
        this.targetUuid = targetUuid;
        this.permissions = permissions;
        this.color = color;
        this.prefix = prefix;
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public JsonObject serialize() {
        return new JsonChain()
                .addProperty("targetUuid", targetUuid.toString())
                .addProperty("permissions", String.join(",", permissions))
                .addProperty("color", color)
                .addProperty("prefix", prefix)
                .get();
    }

    @Override
    public void deserialize(JsonObject object) {
        targetUuid = UUID.fromString(object.get("targetUuid").getAsString());
        permissions = Arrays.asList((object.get("permissions").getAsString()).split("\\s*,\\s*"));
        color = object.get("color").getAsString();
        prefix = object.get("prefix").getAsString();
    }
}
