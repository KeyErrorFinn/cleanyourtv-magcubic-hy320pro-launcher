package com.gathin.tvhome;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Backs up and restores the on-TV choices (theme, dock, night mode, quiet hours and the rest of
 * {@link Config#SETTINGS_NAME}) as one small JSON file, so a reset or a reinstall does not lose the look.
 * The file goes to the app's own folder and, from Android 10, also to Downloads where a file manager can see it.
 * Nothing about the network, accounts or the projector's hardware is in it.
 */
final class LookBackup {
    static final String FILE_NAME = "CleanYourTV-look.json";
    private static final int VERSION = 1;

    private LookBackup() { }

    private static File ownFile(Context c) {
        File dir = c.getExternalFilesDir(null);
        return dir == null ? null : new File(dir, FILE_NAME);
    }

    /** The choices as JSON text. */
    static String export(Context c) throws JSONException {
        SharedPreferences prefs = c.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE);
        JSONObject values = new JSONObject();
        for (Map.Entry<String, ?> e : prefs.getAll().entrySet()) {
            Object v = e.getValue();
            if (v instanceof Boolean || v instanceof Integer || v instanceof Long || v instanceof Float
                    || v instanceof String) {
                values.put(e.getKey(), v);
            }
        }
        return new JSONObject().put("version", VERSION).put("values", values).toString(2);
    }

    /** Writes the backup; returns where it went, for the owner to read. */
    static String save(Context c) throws IOException, JSONException {
        byte[] bytes = export(c).getBytes(StandardCharsets.UTF_8);
        File own = ownFile(c);
        if (own == null) throw new IOException("No storage is available");
        try (FileOutputStream out = new FileOutputStream(own)) {
            out.write(bytes);
        }
        String where = "Android/data/" + c.getPackageName() + "/files/" + FILE_NAME;
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                copyToDownloads(c, bytes);
                where = "Downloads/" + FILE_NAME;
            } catch (RuntimeException | IOException e) {
                // The app's own copy is still saved.
            }
        }
        return where;
    }

    @android.annotation.TargetApi(29)
    private static void copyToDownloads(Context c, byte[] bytes) throws IOException {
        ContentResolver resolver = c.getContentResolver();
        Uri existing = findInDownloads(resolver);
        if (existing != null) resolver.delete(existing, null, null);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, FILE_NAME);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download");
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) throw new IOException("Downloads refused the file");
        try (OutputStream out = resolver.openOutputStream(uri)) {
            if (out == null) throw new IOException("Downloads refused the file");
            out.write(bytes);
        }
    }

    @android.annotation.TargetApi(29)
    private static Uri findInDownloads(ContentResolver resolver) {
        if (Build.VERSION.SDK_INT < 29) return null;
        try (Cursor cursor = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DISPLAY_NAME + "=?",
                new String[]{FILE_NAME}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cursor.getLong(0));
            }
        } catch (RuntimeException e) {
            // No access: treated as not found.
        }
        return null;
    }

    /** The backup text, from the app's own folder or Downloads; null when there is none. */
    static String read(Context c) {
        try {
            File own = ownFile(c);
            if (own != null && own.isFile()) return readAll(new java.io.FileInputStream(own));
            Uri uri = findInDownloads(c.getContentResolver());
            if (uri != null) return readAll(c.getContentResolver().openInputStream(uri));
        } catch (IOException | RuntimeException e) {
            // Falls through to "no backup".
        }
        return null;
    }

    private static String readAll(InputStream in) throws IOException {
        if (in == null) throw new IOException("unreadable");
        try (InputStream input = in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = input.read(buffer)) > 0) out.write(buffer, 0, n);
            return out.toString("UTF-8");
        }
    }

    /** Replaces the choices with the backup's; returns how many were restored, or -1 when the text is not a backup. */
    static int restore(Context c, String text) {
        try {
            JSONObject root = new JSONObject(text);
            if (root.optInt("version", 0) != VERSION) return -1;
            JSONObject values = root.getJSONObject("values");
            SharedPreferences prefs = c.getSharedPreferences(Config.SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putLong(Config.SETTINGS_REVISION_KEY, prefs.getLong(Config.SETTINGS_REVISION_KEY, 0));
            int count = 0;
            java.util.Iterator<String> keys = values.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object v = values.get(key);
                // The change count belongs to this install: Home notices a restore through it, so it is kept moving.
                if (Config.SETTINGS_REVISION_KEY.equals(key)) continue;
                if (v instanceof Boolean) editor.putBoolean(key, (Boolean) v);
                else if (v instanceof Integer) editor.putInt(key, (Integer) v);
                else if (v instanceof Long) editor.putLong(key, (Long) v);
                else if (v instanceof Double) editor.putFloat(key, ((Double) v).floatValue());
                else if (v instanceof String) editor.putString(key, (String) v);
                else continue;
                count++;
            }
            editor.apply();
            return count;
        } catch (JSONException e) {
            return -1;
        }
    }
}
