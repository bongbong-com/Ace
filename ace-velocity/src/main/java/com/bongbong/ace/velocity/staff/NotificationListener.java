package com.bongbong.ace.velocity.staff;

import com.bongbong.ace.shared.redis.IncomingPacketHandler;
import com.bongbong.ace.shared.redis.PacketListener;
import com.bongbong.ace.shared.redis.packets.BasicCommandPacket;
import com.bongbong.ace.shared.redis.packets.UpdatePermissionsPacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationListener implements PacketListener {
    private final StaffManager staffManager;

    @IncomingPacketHandler
    public void onFeedCommand(BasicCommandPacket packet) {
        Notification notification = new Notification(
                packet.getSenderName(), Notification.Type.BASIC, packet.getMessage(), packet.getOrginServer(), null);
        staffManager.sendNotification(notification);
    }
    @IncomingPacketHandler
    public void onPunishmentAudit(UpdatePermissionsPacket packet) {

    }

}
