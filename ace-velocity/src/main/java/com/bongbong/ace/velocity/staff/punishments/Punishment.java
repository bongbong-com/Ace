package com.bongbong.ace.velocity.staff.punishments;

import com.bongbong.ace.shared.utils.DateFormatter;
import com.bongbong.ace.shared.utils.TimeUtil;
import com.bongbong.ace.velocity.PunishmentSettings;
import lombok.Data;
import org.bson.Document;

import java.util.*;

public @Data class Punishment {

    public enum Type {
        BAN, BLACKLIST, KICK, MUTE;

        public String pastMessage() {
            switch (this) {
                case BAN:
                    return "banned";
                case BLACKLIST:
                    return "blacklisted";
                case KICK:
                    return "kicked";
                case MUTE:
                    return "muted";
                default:
                    return "null";
            }
        }
    }

    private final String id;
    private UUID victim, issuer, pardoner;
    private String issueReason, pardonReason, category, orginServer;
    private Date issued, expires, pardoned;
    private Type type;
    private boolean broadcast;

    public String expiry() {
        return expires == null ? "Never" : expires.toString();
    }

    public String typeMessage() {
        return type.equals(Type.KICK)
                ? type.pastMessage()
                : (isActive() ? (getExpires() == null ? "permanently " : "temporarily ") : "un")
                + type.pastMessage();
    }

    public String duration() {
        return expires == null ? "Permanent" : TimeUtil.formatTimeMillis(expires.getTime() - System.currentTimeMillis());
    }

    public String originalDuration() {
        return expires == null ? "Permanent" : TimeUtil.formatTimeMillis(expires.getTime() - issued.getTime());
    }

    public boolean isActive() {
        if (pardoned != null) return false;
        return expires == null || !expires.before(new Date());
    }

    public List<String> getMessage(PunishmentSettings settings) {
        String dateFormatted = DateFormatter.format(getIssued());
        String expireFormatted = null;

        boolean permanent = getExpires() == null;
        if (!permanent) expireFormatted = DateFormatter.format(getExpires());

        int index = -1;

        if (category != null) {
            int i = 0;
            for (String reason : settings.REASONS.split(";")) {
                if (category.equalsIgnoreCase(reason)) index = i;
                i++;
            }
        }

        switch (getType()) {
            case BAN: {
                return Arrays.asList(
                        "&c&lYour account has been " + typeMessage(),
                        "&7" + dateFormatted + " - #" + getId(),
                        "",
                        "This ban will " + (permanent ? "never expire (permanent)" : "expire in " + duration()),
                        "",
                        "Our Moderation and Rule Enforcement Team has determined that",
                        "your account recently participated in activities or behaviors",
                        "that violate the server's Community Guidelines.",
                        index == -1 ? "" : "\n" + settings.MESSAGE.split(";")[index] + "\n",
                        "Require support? Talk to us at https://www.bongbong.com/support");
            }
            case BLACKLIST: {
                return Arrays.asList(
                        "&4&lYour account has been blacklisted.",
                        "&7 " + dateFormatted + " - #" + getId(),
                        "",
                        "Our moderation team has determined that you have",
                        "participated in activities considered to be flagrant",
                        "violations of the server's Community Guidelines.",
                        "",
                        "Community guidelines are strictly enforced to protect",
                        "all members of the community from harmful content.",
                        "",
                        "Require support? Talk to us at https://www.bongbong.com/support");
            }
            case MUTE: {
                return Arrays.asList(
                        "",
                        "&cYour account is muted for " + duration() + " (until " + expiry() + ").",
                        "&7 " + dateFormatted + " - #" + getId(),
                        index == -1 ? "" : "\n" + settings.MESSAGE.split(";")[index] + "\n");
            }
            case KICK: {
                return Arrays.asList(
                        "You have been kicked by a staff member",
                        "Reason: " + getIssueReason());
            }
            default:
                return Arrays.asList("An error occurred whilst logging in (Err Code: ACE4)", "Please report this to administrators immediately.");
        }
    }

    public void importFromDocument(Document document) {
        setVictim(document.get("victim", UUID.class));
        setIssuer(document.get("issuer", UUID.class));
        setPardoner(document.get("pardoner", UUID.class));

        setIssueReason(document.getString("issue_reason"));
        setPardonReason(document.getString("pardon_reason"));
        setCategory(document.getString("category"));
        setIssued(document.getDate("issued"));
        setExpires(document.getDate("expires"));
        setPardoned(document.getDate("pardoned"));
        setType(Type.valueOf(document.getString("type")));
    }

    public Map<String, Object> exportToDocument() {
        Map<String, Object> map = new HashMap<>();
        map.put("victim", getVictim());
        map.put("issuer", getIssuer());
        map.put("pardoner", getPardoner());

        map.put("issue_reason", getIssueReason());
        map.put("pardon_reason", getPardonReason());
        map.put("category", category);
        map.put("issued", getIssued());
        map.put("expires", getExpires());
        map.put("pardoned", getPardoned());
        map.put("type", getType().toString());
        return map;
    }
}

