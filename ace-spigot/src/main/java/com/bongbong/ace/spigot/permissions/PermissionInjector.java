package com.bongbong.ace.spigot.permissions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PermissionInjector {

    private static Field HUMAN_ENTITY_PERMISSIBLE_FIELD;
    private static Field PERMISSIBLE_BASE_ATTACHMENTS_FIELD;


    public static void inject(Plugin plugin, Player player, CorePermissibleBase corePermissible) {
        Class<?> server = Bukkit.getServer().getClass();
        Matcher matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$").matcher(server.getName());
        String serverPackageVersion;
        if (matcher.matches()) serverPackageVersion = '.' + matcher.group(1) + '.';
        else serverPackageVersion = ".";

        try {
            HUMAN_ENTITY_PERMISSIBLE_FIELD = Class.forName("org.bukkit.craftbukkit" + serverPackageVersion + "entity.CraftHumanEntity").getDeclaredField("perm");
            HUMAN_ENTITY_PERMISSIBLE_FIELD.setAccessible(true);

            PERMISSIBLE_BASE_ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            PERMISSIBLE_BASE_ATTACHMENTS_FIELD.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            PermissibleBase permissibleBase = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);

            if (permissibleBase instanceof CorePermissibleBase) return;

            List<PermissionAttachment> attachments = (List<PermissionAttachment>) PERMISSIBLE_BASE_ATTACHMENTS_FIELD.get(permissibleBase);

            for (PermissionAttachment pa : attachments)
                for (Map.Entry<String, Boolean> entry : pa.getPermissions().entrySet())
                    corePermissible.addAttachment(plugin, entry.getKey(), entry.getValue());

            attachments.clear();
            permissibleBase.clearPermissions();

            HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, corePermissible);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
