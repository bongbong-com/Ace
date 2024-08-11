package com.bongbong.ace.spigot.permissions;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class CorePermissibleBase extends PermissibleBase {

    final Player player;

    public CorePermissibleBase(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean hasPermission(String inName) {
        if (isOp()) return true;

        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String p = permission.getPermission().toLowerCase();
            if (p.equals("*")) return true;
        }

        return super.hasPermission(inName);
    }
}
