package com.bongbong.ace.shared.redis;

import com.bongbong.ace.shared.redis.packets.BasicCommandPacket;
import com.bongbong.ace.shared.redis.packets.UpdatePermissionsPacket;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class RedisManager {
    private static final JsonParser PARSER = new JsonParser();
    private final String channel;
    private final JedisPool jedisPool;
    private JedisPubSub jedisPubSub;
    private final List<PacketListenerData> packetListeners;
    private final Map<Integer, Class> idToType = new HashMap<>();
    private final Map<Class, Integer> typeToId = new HashMap<>();

    public RedisManager(String channel, String host, int port, String username, String password) {
        this.channel = channel;
        this.packetListeners = new ArrayList<>();

        if (username == null || password == null) this.jedisPool = new JedisPool(host, port);
        else this.jedisPool = new JedisPool(host, port, username, password);

        this.setupPubSub();

        registerPacket(BasicCommandPacket.class);
        registerPacket(UpdatePermissionsPacket.class);
    }

    public void sendPacket(Packet packet) {
        JsonObject object = packet.serialize();

        if (object == null) throw new IllegalStateException("Packet cannot generate null serialized data");

        Jedis jedis = this.jedisPool.getResource();
        jedis.publish(this.channel, packet.id() + ";" + object);
    }

    private Packet buildPacket(int id) {
        if (!idToType.containsKey(id)) throw new IllegalStateException("A packet with that ID does not exist");

        try {
            return (Packet) idToType.get(id).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new IllegalStateException("Could not create new instance of packet type");
    }

    private void registerPacket(Class clazz) {
        int id;

        try {
            id = (int) clazz.getDeclaredMethod("id").invoke(clazz.newInstance(), null);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (idToType.containsKey(id) || typeToId.containsKey(clazz))
            throw new IllegalStateException("A packet with that ID has already been registered");

        idToType.put(id, clazz);
        typeToId.put(clazz, id);
    }

    public void registerListener(PacketListener packetListener) {
        for (Method method : packetListener.getClass().getDeclaredMethods()) {
            if (method.getDeclaredAnnotation(IncomingPacketHandler.class) == null) continue;

            if (method.getParameters().length > 0 && Packet.class.isAssignableFrom(method.getParameters()[0].getType())) {
                Class packetClass = method.getParameters()[0].getType();
                this.packetListeners.add(new PacketListenerData(packetListener, method, packetClass));
            }
        }
    }

    private void setupPubSub() {
        this.jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (!channel.equalsIgnoreCase(RedisManager.this.channel)) return;

                String[] args = message.split(";");
                int id = Integer.parseInt(args[0]);
                Packet packet = buildPacket(id);

                if (packet == null) return;

                packet.deserialize(PARSER.parse(args[1]).getAsJsonObject());

                for (PacketListenerData data : packetListeners) {
                    if (!data.matches(packet)) continue;

                    try {
                        data.getMethod().invoke(data.getInstance(), packet);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        ForkJoinPool.commonPool().execute(() -> {
            Jedis jedis = this.jedisPool.getResource();
            jedis.subscribe(this.jedisPubSub, channel);
        });
    }

}