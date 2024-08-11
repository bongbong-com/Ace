package com.bongbong.ace.velocity.staff.punishments;

import com.bongbong.ace.velocity.PunishmentSettings;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;

@RequiredArgsConstructor
public class PunishmentUtilities {
    protected final PunishmentSettings settings;

    public AbstractMap.SimpleEntry<Boolean, String> broadcast(String[] args, boolean isTemp) {
        StringBuilder sb = new StringBuilder();
        int start = isTemp ? 1 : 0;
        boolean broadcast = false;
        for (int i = start; i < args.length; i++) {
            String s = args[i];
            if (s.equalsIgnoreCase("-b")) broadcast = true;
            else {
                sb.append(args[i]);
                if (i + 1 != args.length) sb.append(" ");
            }
        }

        return new AbstractMap.SimpleEntry<>(broadcast, sb.toString());
    }

    public String getCategory(String reason) {
        String category = null;
        String[] list = settings.KEYWORDS.split(";");

        outerloop:
        for (String keywords : list) {
            String[] wordList = keywords.split(",");

            int i = 0;
            for (String keyword : wordList) {
                if (reason.contains(keyword)) {
                    category = settings.REASONS.split(";")[i];
                    break outerloop;
                }

                i++;
            }
        }

        return category;
    }
}
