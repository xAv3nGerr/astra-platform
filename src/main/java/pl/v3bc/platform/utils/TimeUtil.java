package pl.v3bc.platform.utils;

import lombok.Getter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: v3bc_
 * @Date: 8/22/26
 * @Project: astra-platform
 */

public class TimeUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    private static final TimeDivision[] TIME_DIVISIONS =
            new TimeDivision[]{
                    TimeDivision.YEAR,
                    TimeDivision.MONTH,
                    TimeDivision.DAY,
                    TimeDivision.HOUR,
                    TimeDivision.MINUTE,
                    TimeDivision.SECOND
            };

    public static Date dateFromString(final String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }

    public static String formatDate(final Instant instant) {
        final Date date = Date.from(instant);
        return DATE_FORMAT.format(date);
    }

    public static String formatDate(final Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTimeSimple(final Duration duration) {
        return formatTimeSimple(duration.toMillis(), true);
    }

    public static String formatTimeSimple(final long millis) {
        return formatTimeSimple(millis, true);
    }

    public static String formatTimeSimple(final long millis, final boolean appendSeconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        if (seconds <= 0L) {
            return (int) millis + "ms";
        }
        long minutes = seconds / 60L;
        seconds %= 60L;
        long hours = minutes / 60L;
        minutes %= 60L;
        long day = hours / 24L;
        hours %= 24L;
        final long years = day / 365L;
        day %= 365L;
        final StringBuilder time = new StringBuilder();
        if (years != 0L) {
            time.append(years).append("r ");
        }
        if (day != 0L) {
            time.append(day).append("d ");
        }
        if (hours != 0L) {
            time.append(hours).append("h ");
        }
        if (minutes != 0L) {
            time.append(minutes).append("m ");
        }
        if (seconds != 0L && appendSeconds) {
            time.append(seconds).append("s");
        }
        return time.toString().trim();
    }

    public static String formatTime(final Duration duration) {
        return formatTime(duration, " ");
    }

    public static String formatTime(final Duration duration, final String delimiter) {
        return formatTime(duration, delimiter, TIME_DIVISIONS);
    }

    public static String formatTime(
            final Duration time, final String delimiter, final TimeDivision[] durationDivisions) {
        long millis = time.toMillis();
        final LinkedHashMap<TimeDivision, Long> timeParts = new LinkedHashMap<>();
        for (final TimeDivision division : durationDivisions) {
            final long divisionTime = millis / division.getMillis();
            millis -= divisionTime * division.getMillis();
            timeParts.put(division, divisionTime);
        }
        return parseTimeParts(timeParts, delimiter);
    }

    private static String parseTimeParts(
            final LinkedHashMap<TimeDivision, Long> timeParts, final String delimiter) {
        final StringBuilder timeStringBuilder = new StringBuilder();
        for (final Map.Entry<TimeDivision, Long> timePart : timeParts.entrySet()) {
            final long partValue = timePart.getValue().longValue();
            if (partValue == 0L) {
                continue;
            }
            timeStringBuilder.append(delimiter).append(timePart.getKey().getFormatted(partValue));
        }
        if (timeStringBuilder.isEmpty()) {
            return TimeDivision.SECOND.getFormatted(0L);
        }
        return timeStringBuilder.substring(delimiter.length());
    }

    public static Duration parseTime(final String time) {
        final StringBuilder tempNumber = new StringBuilder();
        long resultTime = 0L;
        final char[] stringChars = time.toLowerCase().toCharArray();
        int i;
        label46:
        for (i = 0; i < stringChars.length; i++) {
            final char c = stringChars[i];
            if (c >= '0' && c <= '9') {
                tempNumber.append(c);
            } else {
                for (final TimeDivision timeDivision : TimeDivision.values()) {
                    label43:
                    for (final String abbreviation : timeDivision.getAbbreviations()) {
                        if (i + abbreviation.length() <= stringChars.length) {
                            final char[] abbreviationChars = abbreviation.toCharArray();
                            for (int a = 0; a < abbreviationChars.length; a++) {
                                if (abbreviationChars[a] != stringChars[i + a]) {
                                    continue label43;
                                }
                            }
                            char next = '0';
                            if (i + abbreviation.length() < stringChars.length) {
                                next = stringChars[i + abbreviation.length()];
                            }
                            if (next >= '0' && next <= '9') {
                                if (tempNumber.isEmpty()) {
                                    return Duration.ofSeconds(0L);
                                }
                                resultTime += Long.parseLong(tempNumber.toString()) * timeDivision.getMillis();
                                tempNumber.setLength(0);
                                i += abbreviation.length() - 1;
                                continue label46;
                            }
                        }
                    }
                }
                return Duration.ofSeconds(0L);
            }
        }
        if (!tempNumber.isEmpty()) {
            return Duration.ofSeconds(0L);
        }
        return Duration.ofMillis(resultTime);
    }

    public enum TimeDivision {
        SECOND(1000L, "sekunda", "sekundy", "sekund", "s", "sek"),
        MINUTE(60000L, "minuta", "minuty", "minut", "m", "min"),
        HOUR(3600000L, "godzina", "godziny", "godzin", "h", "godz"),
        DAY(86400000L, "dzien", "dni", "dni", "d", "dni", "day"),
        WEEK(604800000L, "tydzien", "tygodnie", "tygodni", "w", "t", "tyg"),
        MONTH(2592000000L, "miesiac", "miesiace", "miesiecy", "mo", "ms", "mc", "mies"),
        YEAR(31536000000L, "rok", "lata", "lata", "y", "r", "l", "lat", "rok");
        @Getter
        private final long millis;
        private final String singularForm;
        private final String doubleForm;
        private final String pluralForm;
        private final String[] abbreviations;

        TimeDivision(
                final long millis,
                final String singularForm,
                final String doubleForm,
                final String pluralForm,
                final String... abbreviations) {
            this.millis = millis;
            this.singularForm = singularForm;
            this.doubleForm = doubleForm;
            this.pluralForm = pluralForm;
            this.abbreviations = abbreviations;
        }

        public String getForm(final long amount) {
            if (amount == 1L) {
                return singularForm;
            }
            final long onesNumber = amount % 10L;
            final long tensNumber = amount % 100L;
            if (onesNumber < 2L || onesNumber > 4L) {
                return pluralForm;
            }
            if (tensNumber >= 12L && tensNumber <= 14L) {
                return pluralForm;
            }
            return doubleForm;
        }

        public String getFormatted(final long amount) {
            return amount + " " + this.getForm(amount);
        }

        public String[] getAbbreviations() {
            return Arrays.copyOf(abbreviations, abbreviations.length);
        }
    }


    public static String formatPlayingTime(long millis) {
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        }
        long days = TimeUnit.SECONDS.toDays(totalSeconds);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(totalSeconds));
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || (days == 0 && hours == 0)) {
            sb.append(minutes).append("m");
        }
        return sb.toString().trim();
    }

    public static String formatPlayingTime(java.time.Duration duration) {
        return formatPlayingTime(duration.toMillis());
    }

    public static long deserialize(String string) {
        long l = 0L;
        Pattern pattern = Pattern.compile("(\\d+)([dhms])");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String string2;
            int n = Integer.parseInt(matcher.group(1));
            switch (string2 = matcher.group(2)) {
                case "d": {
                    l += TimeUnit.DAYS.toMillis(n);
                    continue;
                }
                case "h": {
                    l += TimeUnit.HOURS.toMillis(n);
                    continue;
                }
                case "m": {
                    l += TimeUnit.MINUTES.toMillis(n);
                    continue;
                }
                case "s": {
                    l += TimeUnit.SECONDS.toMillis(n);
                    continue;
                }
            }
            throw new IllegalArgumentException("Unknown time unit: " + string2);
        }
        return l;
    }
}
