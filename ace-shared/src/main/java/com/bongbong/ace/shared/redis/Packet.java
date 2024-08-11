package com.bongbong.ace.shared.redis;

import com.google.gson.JsonObject;

public interface Packet {

    int id();

    JsonObject serialize();

    void deserialize(JsonObject object);

}