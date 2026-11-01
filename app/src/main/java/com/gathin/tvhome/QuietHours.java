package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * Quiet hours: a cap on the TV's media volume, always or between two times each day. The choices live with the other
 * on-TV choices ({@link Config#SETTINGS_NAME}); {@link QuietHoursService} enforces them. Plain logic, no Android
 * types except the reading and writing of the choices.
 */
final class QuietHours {
    static final String MODE_KEY = "quiet_mode", CAP_KEY = "quiet_cap", START_KEY = "quiet_start", END_KEY = "quiet_end",
            HEADPHONES_KEY = "quiet_headphones_free";
    static final int OFF = 0, SCHEDULED = 1, ALWAYS = 2;
    static final int DAY_MINUTES = 24 * 60, TIME_STEP = 15, CAP_STEP = 5, MIN_CAP = 5;

    int mode = OFF;
    /** The highest volume allowed, in percent of the media volume range. */
    int cap = 20;
    int start = 20 * 60;
    int end = 7 * 60;
    /** Headphones do not wake the room: no cap while sound goes to them. */
    boolean headphonesFree = true;

    static QuietHours load(Context c) {
        SharedPreferences p = c.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE);
        QuietHours q = new QuietHours();
        q.mode = p.getInt(MODE_KEY, q.mode);
        q.cap = p.getInt(CAP_KEY, q.cap);
        q.start = p.getInt(START_KEY, q.start);
        q.end = p.getInt(END_KEY, q.end);
        q.headphonesFree = p.getBoolean(HEADPHONES_KEY, q.headphonesFree);
        return q.clamped();
    }

    /** Writes the choices without bumping the settings change count: nothing on Home depends on them. */
    void save(Context c) {
        clamped();
        c.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE).edit()
                .putInt(MODE_KEY, mode).putInt(CAP_KEY, cap).putInt(START_KEY, start).putInt(END_KEY, end)
                .putBoolean(HEADPHONES_KEY, headphonesFree).apply();
    }

    QuietHours clamped() {
        mode = clamp(mode, OFF, ALWAYS);
        cap = clamp(Math.round(cap / (float) CAP_STEP) * CAP_STEP, MIN_CAP, 100);
        start = wrap(Math.round(start / (float) TIME_STEP) * TIME_STEP);
        end = wrap(Math.round(end / (float) TIME_STEP) * TIME_STEP);
        return this;
    }

    /** True from start up to, not including, end; a window may run past midnight. start == end is never. */
    static boolean inWindow(int start, int end, int minute) {
        if (start == end) return false;
        return start < end ? minute >= start && minute < end : minute >= start || minute < end;
    }

    boolean capNow(int minute) {
        return mode == ALWAYS || (mode == SCHEDULED && inWindow(start, end, minute));
    }

    /** The cap as a volume step of a stream that goes 0 to max: at least 1, so a cap is never silence. */
    static int capIndex(int capPercent, int max) {
        if (max <= 0) return 0;
        return clamp(Math.round(capPercent * max / 100f), 1, max);
    }

    /** Never while muted: setting a muted stream's volume can unmute it, and the unmute is itself a change. */
    boolean mustLower(int minute, int volume, int max, boolean onHeadphones, boolean fixedVolume, boolean muted) {
        if (fixedVolume || muted || !capNow(minute)) return false;
        if (onHeadphones && headphonesFree) return false;
        return volume > capIndex(cap, max);
    }

    /** Minutes until the cap can start or stop by itself; -1 when it never does on its own. */
    int minutesToNextChange(int minute) {
        if (mode != SCHEDULED || start == end) return -1;
        int toStart = wrap(start - minute);
        int toEnd = wrap(end - minute);
        if (toStart == 0) toStart = DAY_MINUTES;
        if (toEnd == 0) toEnd = DAY_MINUTES;
        return Math.min(toStart, toEnd);
    }

    static String clock(int minute, boolean h24) {
        int h = minute / 60, m = minute % 60;
        if (h24) return String.format(Locale.ROOT, "%02d:%02d", h, m);
        return String.format(Locale.ROOT, "%d:%02d %s", h % 12 == 0 ? 12 : h % 12, m, h < 12 ? "AM" : "PM");
    }

    static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    static int wrap(int minute) {
        return ((minute % DAY_MINUTES) + DAY_MINUTES) % DAY_MINUTES;
    }
}
