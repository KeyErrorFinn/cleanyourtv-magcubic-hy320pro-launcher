package com.gathin.tvhome;

import android.content.res.Resources;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * A festive skin, from a theme's config.json ({@code festival}, {@code festival_until}, {@code greeting} and
 * {@code accent_color}) or from the seasonal setting (see {@link Seasonal}). While it is active the pills get
 * festive icons (a diya for the network, a bell for Inputs, a marigold for Settings; each {@link Season} has its own
 * three), the greeting is replaced and the focus glow takes the accent colour. It switches itself off after
 * {@code festival_until}, so nobody has to remember to undo it.
 */
final class Festival {

    static final Festival NONE = new Festival("", null, null, "", 0, false);

    /** Warm diya gold, the accent when a theme names a festival but no colour. */
    static final int DEFAULT_ACCENT = 0xFFFFB23C;

    /** Lower-case festival name, or "" for none. */
    final String name;
    /** The holiday this skin is for when the name is one of {@link Season}'s ids, else null (the diya's skin). */
    final Season season;
    /** Last day the skin shows, inclusive; null for no end date. */
    final LocalDate until;
    /** Text that replaces the time-of-day greeting; "" uses the festival's own default. */
    final String greeting;
    /** Focus glow colour as 0xAARRGGBB, or 0 for the festival's own default. */
    final int accent;
    /** Whether the seasonal setting picked this skin (rather than a theme naming it), so the calendar can replace it. */
    final boolean seasonal;

    private Festival(String name, Season season, LocalDate until, String greeting, int accent, boolean seasonal) {
        this.name = name;
        this.seasonal = seasonal;
        this.season = season;
        this.until = until;
        this.greeting = greeting;
        this.accent = accent;
    }

    /**
     * Builds the skin from raw config values. Anything malformed turns the skin off rather than leaving a
     * festival on the screen forever: an unreadable end date or an empty name gives {@link #NONE}.
     */
    static Festival parse(String name, String until, String greeting, String accent) {
        String n = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        if (n.isEmpty() || n.length() > 40) return NONE;
        LocalDate end = null;
        if (until != null && !until.trim().isEmpty()) {
            try {
                end = LocalDate.parse(until.trim());
            } catch (DateTimeParseException e) {
                return NONE;
            }
        }
        String g = greeting == null ? "" : Cards.cleanLabel(greeting, "");
        Season season = Season.fromId(n);
        return new Festival(season != null ? season.id : n, season, end, g, parseColor(accent), false);
    }

    /** The skin for {@code season}, ending after {@code until} (inclusive), or with no end date when null. */
    static Festival ofSeason(Season season, LocalDate until) {
        return new Festival(season.id, season, until, "", 0, true);
    }

    /** Whether the skin shows today. */
    boolean active(LocalDate today) {
        return !name.isEmpty() && (until == null || !today.isAfter(until));
    }

    private static int linesFor(Season season) {
        switch (season) {
            case NEW_YEAR:
                return R.array.festival_new_year_lines;
            case VALENTINES:
                return R.array.festival_valentines_lines;
            case ST_PATRICKS:
                return R.array.festival_st_patricks_lines;
            case EASTER:
                return R.array.festival_easter_lines;
            case HALLOWEEN:
                return R.array.festival_halloween_lines;
            default:
                return R.array.festival_christmas_lines;
        }
    }

    /** The greeting to show while active, or "" to keep the normal time-of-day greeting. */
    String greetingText(Resources res) {
        return greetingText(res, null);
    }

    /**
     * Like {@link #greetingText(Resources)}, but a season's skin says its wish ("Happy Halloween") only on the day
     * itself; on the other days of its run it picks one of its themed lines, which changes every few hours.
     */
    String greetingText(Resources res, LocalDate today) {
        if (!greeting.isEmpty()) return greeting;
        if (season != null && today != null && !season.isHoliday(today)) {
            String[] lines = res.getStringArray(linesFor(season));
            if (lines.length > 0) return lines[Math.floorMod((today.getYear() * 366 + today.getDayOfYear()) * 8 + java.time.LocalTime.now().getHour() / 3, lines.length)];
        }
        if (season != null) {
            switch (season) {
                case NEW_YEAR:
                    return res.getString(R.string.festival_new_year_greeting);
                case VALENTINES:
                    return res.getString(R.string.festival_valentines_greeting);
                case ST_PATRICKS:
                    return res.getString(R.string.festival_st_patricks_greeting);
                case EASTER:
                    return res.getString(R.string.festival_easter_greeting);
                case HALLOWEEN:
                    return res.getString(R.string.festival_halloween_greeting);
                default:
                    return res.getString(R.string.festival_christmas_greeting);
            }
        }
        switch (name) {
            case "dussehra":
                return res.getString(R.string.festival_dussehra_greeting);
            case "diwali":
                return res.getString(R.string.festival_diwali_greeting);
            default:
                return "";
        }
    }

    int accentColor() {
        if (accent != 0) return accent;
        return season != null ? season.accent : DEFAULT_ACCENT;
    }

    /** {@code #RRGGBB} to an opaque colour, anything else to 0. */
    static int parseColor(String s) {
        if (s == null) return 0;
        String t = s.trim();
        if (!t.matches("#[0-9A-Fa-f]{6}")) return 0;
        return 0xFF000000 | Integer.parseInt(t.substring(1), 16);
    }
}
