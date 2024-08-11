package com.bongbong.ace.spigot.permissions;

import com.bongbong.ace.shared.redis.IncomingPacketHandler;
import com.bongbong.ace.shared.redis.PacketListener;
import com.bongbong.ace.shared.redis.packets.UpdatePermissionsPacket;
import com.bongbong.ace.spigot.utils.Colors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class PermissionPacketListener implements PacketListener {
    final Plugin plugin;

    @IncomingPacketHandler
    public void onUpdatePermissions(UpdatePermissionsPacket packet) {
//        System.out.println("Yay! " + packet.getPermissions());

        Player player = Bukkit.getPlayer(packet.getTargetUuid());
        if (player == null) return;

        PermissionAttachment permissionAttachment = player.addAttachment(plugin);

        //Take away every permission from player
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions())
            permissionAttachment.setPermission(permission.getPermission(), false);

        for (String permission : packet.getPermissions())
            permissionAttachment.setPermission(permission, true);

        player.setDisplayName(Colors.translate(packet.getColor() + player.getName() + "&f"));
    }
}
