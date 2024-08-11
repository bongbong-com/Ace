package com.bongbong.ace.shared.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    public static String formatTimeMillis(long millis) {
        long seconds = (millis / 1000L) + 1;

        if (seconds < 1) return "0 seconds";

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        long years = day / 365;
        day = day % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) time.append(years).append("y ");
        if (day != 0) time.append(day).append("d ");
        if (hours != 0) time.append(hours).append("h ");
        if (minutes != 0) time.append(minutes).append("m ");
        if (seconds != 0) time.append(seconds).append("s ");

        return time.toString().trim();
    }

    public static Date getTime(String arg) {
        Pattern p = Pattern.compile("[a-z]+|\\d+");
        Matcher m = p.matcher(arg.toLowerCase());

        int time = -1;
        String type = null;
        boolean isTemp = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        while (m.find()) {
            String a = m.group();
            try {
                time = Integer.parseInt(a);
                if (time < 1) {
                    time = -1;
                }
            } catch (NumberFormatException ignored) {
                type = a;
            }

            if (time > 0 && type != null) {
                switch (type) {
                    case "seconds":
                    case "second":
                    case "sec":
                    case "s":
                        calendar.add(Calendar.SECOND, time);
                        break;
                    case "minutes":
                    case "minute":
                    case "m":
                        calendar.add(Calendar.MINUTE, time);
                        break;
                    case "hours":
                    case "hrs":
                    case "hr":
                    case "h":
                        calendar.add(Calendar.HOUR, time);
                        break;
                    case "days":
                    case "day":
                    case "d":
                        calendar.add(Calendar.HOUR, time * 24);
                        break;
                    case "weeks":
                    case "week":
                    case "w":
                        calendar.add(Calendar.HOUR, time * 24 * 7);
                        break;
                    case "months":
                    case "month":
                    case "mo":
                        calendar.add(Calendar.MONTH, time);
                        break;
                }

                isTemp = true;
                time = -1;
                type = null;
            }
        }

        return isTemp ? calendar.getTime() : null;
    }

}