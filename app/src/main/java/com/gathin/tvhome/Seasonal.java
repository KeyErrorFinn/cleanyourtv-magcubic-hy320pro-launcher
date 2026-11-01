package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.Locale;

/**
 * The seasonal setting: {@code off}, {@code auto} (each holiday on its own dates) or the name of one
 * {@link Season} to keep for as long as it stays chosen. Advanced edition: picked in Settings, default
 * Automatic. Basic edition: off unless config.xml ({@code seasonal_mode}) or a theme's config.json
 * ({@code "seasonal"}) turns it on. A festival a theme names itself ({@code festival}) still wins while it runs.
 */
final class Seasonal {

    static final String OFF = "off";
    static final String AUTO = "auto";
    /** The saved choice in {@link Config#SETTINGS_NAME}; absent means "use the edition's default". */
    static final String KEY = "seasonal_mode";
    /** Settings' switch for the wallpaper's movement (Advanced edition); on unless it was turned off. */
    static final String ANIMATE_KEY = "seasonal_animate";

    private Seasonal() {
    }

    /** {@code raw} as a mode ({@link #OFF}, {@link #AUTO} or a season id), or {@code fallback} when it is none of them. */
    static String normalise(String raw, String fallback) {
        if (raw == null) return fallback;
        String n = raw.trim().toLowerCase(Locale.ROOT);
        if (n.equals(OFF) || n.equals(AUTO)) return n;
        Season s = Season.fromId(n);
        return s != null ? s.id : fallback;
    }

    /**
     * The mode in force: the saved choice (Advanced only, where Settings can make one), else the theme's
     * {@code seasonal}, else config.xml's, else the edition's default. Each is skipped when empty or unknown.
     */
    static String mode(String saved, String theme, String configured, String editionDefault) {
        String fallback = normalise(editionDefault, OFF);
        for (String candidate : new String[] {saved, theme, configured}) {
            if (candidate == null || candidate.trim().isEmpty()) continue;
            String n = normalise(candidate, null);
            if (n != null) return n;
        }
        return fallback;
    }

    /**
     * The skin to show: the one a theme names itself while it runs, otherwise whatever {@code mode} gives for
     * {@code today}.
     */
    static Festival pick(Festival theme, String mode, LocalDate today) {
        return theme.active(today) ? theme : festival(mode, today);
    }

    /** The festival for {@code mode} on {@code today}: none when off or between holidays, else its skin. */
    static Festival festival(String mode, LocalDate today) {
        Season season = season(mode, today);
        if (season == null) return Festival.NONE;
        // A fixed season has no end date; an automatic one ends with its window.
        LocalDate until = AUTO.equals(normalise(mode, OFF)) ? season.endOfWindowHolding(today) : null;
        return Festival.ofSeason(season, until);
    }

    /** The season to show for {@code mode} on {@code today}, or null. */
    static Season season(String mode, LocalDate today) {
        String m = normalise(mode, OFF);
        if (m.equals(OFF)) return null;
        if (m.equals(AUTO)) return Season.on(today);
        return Season.fromId(m);
    }

    /** Whether the seasonal wallpaper moves: on by default, off once Settings turned it off. */
    static boolean animated(Context ctx) {
        if (!BuildConfig.ADVANCED_SETTINGS) return true;
        return ctx.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATE_KEY, true);
    }

    /** The saved choice from Settings (Advanced edition), or "" when none was made or this is the Basic edition. */
    static String saved(Context ctx) {
        if (!BuildConfig.ADVANCED_SETTINGS) return "";
        SharedPreferences prefs = ctx.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY, "");
    }
}
