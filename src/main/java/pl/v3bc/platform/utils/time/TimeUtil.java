package pl.v3bc.platform.utils.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TimeUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    private static final TimeDivision[] TIME_DIVISIONS = {TimeDivision.YEAR, TimeDivision.MONTH, TimeDivision.DAY, TimeDivision.HOUR, TimeDivision.MINUTE, TimeDivision.SECOND};

    private TimeUtil() {}

    public static Duration parseTime(String string) {
        if (string == null || string.isEmpty()) {
            return Duration.ZERO;
        }
        StringBuilder stringBuilder = new StringBuilder();
        long totalMillis = 0L;
        String lowerInput = string.toLowerCase();
        for (int i = 0; i < lowerInput.length(); i++) {
            char c = lowerInput.charAt(i);
            if (c >= '0' && c <= '9') {
                stringBuilder.append(c);
                continue;
            }
            boolean matched = false;
            for (TimeDivision timeDivision : TimeDivision.values()) {
                for (String abbreviation : timeDivision.getAbbreviations()) {
                    if (lowerInput.startsWith(abbreviation, i)) {
                        if (stringBuilder.isEmpty()) {
                            return Duration.ZERO;
                        }
                        totalMillis += Long.parseLong(stringBuilder.toString()) * timeDivision.getMillis();
                        stringBuilder.setLength(0);
                        i += abbreviation.length() - 1;
                        matched = true;
                        break;
                    }
                }
                if (matched) {
                    break;
                }
            }
            if (!matched) {
                return Duration.ZERO;
            }
        }
        if (!stringBuilder.isEmpty()) {
            return Duration.ZERO;
        }
        return Duration.ofMillis(totalMillis);
    }

    public static String formatTimeSimple(long l, boolean bl) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(l);
        if (seconds <= 0L) {
            return (int) l + "ms";
        }
        long minutes = seconds / 60L;
        seconds %= 60L;
        long hours = minutes / 60L;
        minutes %= 60L;
        long days = hours / 24L;
        hours %= 24L;
        long years = days / 365L;
        days %= 365L;
        StringBuilder stringBuilder = new StringBuilder();
        if (years != 0L) {
            stringBuilder.append(years).append("r ");
        }
        if (days != 0L) {
            stringBuilder.append(days).append("d ");
        }
        if (hours != 0L) {
            stringBuilder.append(hours).append("h ");
        }
        if (minutes != 0L) {
            stringBuilder.append(minutes).append("m ");
        }
        if (seconds != 0L && bl) {
            stringBuilder.append(seconds).append("s");
        }
        return stringBuilder.toString().trim();
    }

    public static String formatTime(Duration duration) {
        return TimeUtil.formatTime(duration, " ");
    }

    public static String formatTime(Duration duration, String string) {
        return TimeUtil.formatTime(duration, string, TIME_DIVISIONS);
    }

    public static String formatTime(Duration duration, String string, TimeDivision[] timeDivisions) {
        long l = duration.toMillis();
        LinkedHashMap<TimeDivision, Long> linkedHashMap = new LinkedHashMap<>();
        for (TimeDivision timeDivision : timeDivisions) {
            long l2 = l / timeDivision.getMillis();
            l -= l2 * timeDivision.getMillis();
            linkedHashMap.put(timeDivision, l2);
        }
        return TimeUtil.parseTimeParts(linkedHashMap, string);
    }

    private static String parseTimeParts(LinkedHashMap<TimeDivision, Long> linkedHashMap, String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<TimeDivision, Long> entry : linkedHashMap.entrySet()) {
            long l = entry.getValue();
            if (l == 0L) {
                continue;
            }
            stringBuilder.append(string).append(entry.getKey().getFormatted(l));
        }

        if (stringBuilder.isEmpty()) {
            return TimeDivision.SECOND.getFormatted(0L);
        }

        return stringBuilder.substring(string.length());
    }

    public static Date dateFromString(String string) throws ParseException {
        return DATE_FORMAT.parse(string);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatDate(Instant instant) {
        return DATE_FORMAT.format(Date.from(instant));
    }

    public static String formatTimeSimple(Duration duration) {
        return TimeUtil.formatTimeSimple(duration.toMillis(), true);
    }

    public static String formatTimeSimple(long l) {
        return TimeUtil.formatTimeSimple(l, true);
    }
}