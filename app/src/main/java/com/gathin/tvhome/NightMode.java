package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Night mode: between two times each day Home is dimmed, so a paused film or a late browse does not light up the
 * room. The choices live with the other on-TV choices ({@link Config#SETTINGS_NAME}); Home reads them each minute.
 */
final class NightMode {
    static final String MODE_KEY = "night_mode", START_KEY = "night_start", END_KEY = "night_end", DIM_KEY = "night_dim";
    static final int OFF = 0, SCHEDULED = 1, ALWAYS = 2;
    /** How dark the shade is, in percent black. */
    static final int[] DIM_LEVELS = {30, 45, 60, 75};
    static final int DEFAULT_START = 22 * 60, DEFAULT_END = 6 * 60, DEFAULT_DIM = 60;

    private NightMode() { }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    static int mode(Context c) {
        return QuietHours.clamp(prefs(c).getInt(MODE_KEY, OFF), OFF, ALWAYS);
    }

    static int start(Context c) {
        return QuietHours.wrap(prefs(c).getInt(START_KEY, DEFAULT_START));
    }

    static int end(Context c) {
        return QuietHours.wrap(prefs(c).getInt(END_KEY, DEFAULT_END));
    }

    static int dim(Context c) {
        return QuietHours.clamp(prefs(c).getInt(DIM_KEY, DEFAULT_DIM), 10, 90);
    }

    /** True while Home should be dimmed. */
    static boolean active(Context c) {
        int mode = mode(c);
        if (mode == OFF) return false;
        if (mode == ALWAYS) return true;
        Calendar now = Calendar.getInstance();
        return QuietHours.inWindow(start(c), end(c), now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE));
    }
}
