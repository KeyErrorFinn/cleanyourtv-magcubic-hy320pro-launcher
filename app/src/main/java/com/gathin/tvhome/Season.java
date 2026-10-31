package com.gathin.tvhome;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

/**
 * A holiday Home can dress up for, with the dates it shows on while the seasonal setting is Automatic.
 * Windows run a week or so before the day and finish on it (Easter and Christmas a day or two after), and a
 * window is inclusive at both ends. Easter moves, so its window is worked out for each year.
 */
enum Season {
    NEW_YEAR("new_year", 0xFFFFD34D),
    VALENTINES("valentines", 0xFFFF4D79),
    ST_PATRICKS("st_patricks", 0xFF3DDC84),
    EASTER("easter", 0xFFC79BFF),
    HALLOWEEN("halloween", 0xFFFF7A1A),
    CHRISTMAS("christmas", 0xFFE5484D);

    /** Stable name used in settings, themes and config: lower case, underscores. */
    final String id;
    /** Focus glow and greeting colour, as 0xAARRGGBB. */
    final int accent;

    Season(String id, int accent) {
        this.id = id;
        this.accent = accent;
    }

    /** The season named {@code id} (case and "-" or space for "_" are forgiven), or null. */
    static Season fromId(String id) {
        if (id == null) return null;
        String n = id.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        for (Season s : values()) {
            if (s.id.equals(n)) return s;
        }
        return null;
    }

    /** First day of the window that starts in {@code year}. */
    LocalDate start(int year) {
        switch (this) {
            case NEW_YEAR:
                return LocalDate.of(year, Month.DECEMBER, 27);
            case VALENTINES:
                return LocalDate.of(year, Month.FEBRUARY, 7);
            case ST_PATRICKS:
                return LocalDate.of(year, Month.MARCH, 10);
            case EASTER:
                return easterSunday(year).minusDays(9);
            case HALLOWEEN:
                return LocalDate.of(year, Month.OCTOBER, 15);
            default:
                return LocalDate.of(year, Month.DECEMBER, 10);
        }
    }

    /** Last day of the window that starts in {@code year}; New Year's ends in the next year. */
    LocalDate end(int year) {
        switch (this) {
            case NEW_YEAR:
                return LocalDate.of(year + 1, Month.JANUARY, 2);
            case VALENTINES:
                return LocalDate.of(year, Month.FEBRUARY, 14);
            case ST_PATRICKS:
                return LocalDate.of(year, Month.MARCH, 17);
            case EASTER:
                return easterSunday(year).plusDays(1);
            case HALLOWEEN:
                return LocalDate.of(year, Month.NOVEMBER, 1);
            default:
                return LocalDate.of(year, Month.DECEMBER, 26);
        }
    }

    /**
     * The last day of the window that holds {@code day}, or null when this season is not on that day. Checks the
     * windows that start in this year and in the year before, which is how New Year's reaches into January.
     */
    LocalDate endOfWindowHolding(LocalDate day) {
        for (int year = day.getYear(); year >= day.getYear() - 1; year--) {
            if (!day.isBefore(start(year)) && !day.isAfter(end(year))) return end(year);
        }
        return null;
    }

    /**
     * The season on {@code day} in Automatic mode, or null for an ordinary day. Where two windows overlap (an
     * early Easter reaches back into St. Patrick's week) the one that ends first wins, so each holiday gets its
     * own day and the next takes over straight after.
     */
    static Season on(LocalDate day) {
        Season best = null;
        LocalDate bestEnd = null;
        for (Season s : values()) {
            LocalDate end = s.endOfWindowHolding(day);
            if (end != null && (bestEnd == null || end.isBefore(bestEnd))) {
                best = s;
                bestEnd = end;
            }
        }
        return best;
    }

    /** Easter Sunday in the Gregorian calendar (the Western churches' date), by the Meeus/Jones/Butcher method. */
    static LocalDate easterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = (h + l - 7 * m + 114) % 31 + 1;
        return LocalDate.of(year, month, day);
    }
}
