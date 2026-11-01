package com.gathin.tvhome;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * The weather line on Home. The launcher has no network of its own: it reads the last reading the Weather add-on
 * keeps ("Sunny  21°  Sydney"), through that app's read-only provider. Nothing shows until the add-on is installed
 * and has a place; a reading older than {@link #MAX_AGE_MS} is hidden rather than shown wrong.
 */
final class WeatherWidget {
    /** The on-TV choice in {@link Config#SETTINGS_NAME}; on unless switched off. */
    static final String KEY = "weather_widget";
    private static final Uri NOW = Uri.parse("content://io.github.sumyabazar1.weather.provider/now");
    private static final long MAX_AGE_MS = 3 * 60 * 60_000L;

    private WeatherWidget() {
    }

    static boolean enabled(Context c) {
        return c.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE).getBoolean(KEY, true);
    }

    /** The line to show, or null for nothing. */
    static String read(Context c) {
        if (!enabled(c)) return null;
        try (Cursor cursor = c.getContentResolver().query(NOW, null, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) return null;
            long age = System.currentTimeMillis() - cursor.getLong(cursor.getColumnIndexOrThrow("time"));
            if (age < 0 || age > MAX_AGE_MS) return null;
            String sky = cursor.getString(cursor.getColumnIndexOrThrow("sky"));
            String place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
            int temp = cursor.getInt(cursor.getColumnIndexOrThrow("temp"));
            StringBuilder line = new StringBuilder();
            if (sky != null && !sky.isEmpty()) line.append(sky).append("  ");
            line.append(temp).append('°');
            if (place != null && !place.isEmpty()) line.append("  ").append(place);
            return line.toString();
        } catch (RuntimeException e) {
            return null;   // add-on missing, or its provider refused: no weather line
        }
    }
}
