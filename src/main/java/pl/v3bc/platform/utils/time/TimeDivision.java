package pl.v3bc.platform.utils.time;

/**
 * @Author: v3bc_
 * @Date: 9/12/26
 * @Project: astra-platform
 */

public enum TimeDivision {
    YEAR(31536000000L, "r", "rok", "lata", "lat"),
    MONTH(2592000000L, "mc", "miesiac", "miesiace", "miesiecy"),
    DAY(86400000L, "d", "dzien", "dni"),
    HOUR(3600000L, "h", "godz", "godzina", "godziny", "godzin"),
    MINUTE(60000L, "m", "min", "minuta", "minuty", "minut"),
    SECOND(1000L, "s", "sek", "sekunda", "sekundy", "sekund");

    private final long millis;
    private final String[] abbreviations;

    TimeDivision(long millis, String... abbreviations) {
        this.millis = millis;
        this.abbreviations = abbreviations;
    }

    public long getMillis() {
        return this.millis;
    }

    public String[] getAbbreviations() {
        return this.abbreviations;
    }

    public String getFormatted(long value) {
        return value + this.abbreviations[0];
    }
}