package com.gathin.tvhome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.Calendar;

/**
 * Keeps the media volume under the quiet-hours cap: whenever the volume or the output changes it checks, and again
 * at each start and end of the hours. Runs only while quiet hours is on (Home starts it) and stops itself when it is
 * switched off; with it off the launcher has no service at all.
 */
public class QuietHoursService extends Service {
    private static final String CHANNEL = "quiet_hours";
    private static final String VOLUME_CHANGED = "android.media.VOLUME_CHANGED_ACTION";
    private static final String DEVICES_CHANGED = "android.media.STREAM_DEVICES_CHANGED_ACTION";
    private static final long MAX_WAIT_MS = 5 * 60_000L;

    private final Handler main = new Handler(Looper.getMainLooper());
    private AudioManager audio;
    /** Kept in a field: SharedPreferences holds its listeners weakly. */
    private final SharedPreferences.OnSharedPreferenceChangeListener prefsListener = (p, key) -> check();
    private final Runnable tick = this::check;
    private final BroadcastReceiver changes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            check();
        }
    };

    /** Starts the service when quiet hours is on; called whenever Home or its settings come up. */
    static void sync(Context c) {
        if (QuietHours.load(c).mode == QuietHours.OFF) return;
        try {
            c.startForegroundService(new Intent(c, QuietHoursService.class));
        } catch (RuntimeException e) {
            // Android refuses a foreground start from the background; the next time Home is shown starts it.
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audio = getSystemService(AudioManager.class);
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL, "Quiet hours", NotificationManager.IMPORTANCE_MIN));
        Notification n = new Notification.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_settings)
                .setContentTitle("Quiet hours")
                .setContentText("Keeping the volume under the cap you chose")
                .setOngoing(true)
                .build();
        if (Build.VERSION.SDK_INT >= 34) startForeground(7, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        else startForeground(7, n);
        getSharedPreferences(Config.SETTINGS_NAME, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(prefsListener);
        IntentFilter f = new IntentFilter();
        f.addAction(VOLUME_CHANGED);
        f.addAction(DEVICES_CHANGED);
        f.addAction(Intent.ACTION_TIME_CHANGED);
        f.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        f.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(changes, f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        check();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        main.removeCallbacks(tick);
        getSharedPreferences(Config.SETTINGS_NAME, MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(prefsListener);
        unregisterReceiver(changes);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void check() {
        main.removeCallbacks(tick);
        QuietHours q = QuietHours.load(this);
        if (q.mode == QuietHours.OFF) {
            stopSelf();
            return;
        }
        int minute = minuteNow();
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (q.mustLower(minute, volume, max, onHeadphones(audio), audio.isVolumeFixed(),
                audio.isStreamMute(AudioManager.STREAM_MUSIC))) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, QuietHours.capIndex(q.cap, max), AudioManager.FLAG_SHOW_UI);
        }
        long wait = MAX_WAIT_MS;
        int next = q.minutesToNextChange(minute);
        if (next > 0) {
            Calendar now = Calendar.getInstance();
            wait = Math.min(wait, next * 60_000L - now.get(Calendar.SECOND) * 1000L - now.get(Calendar.MILLISECOND));
        }
        main.postDelayed(tick, Math.max(1000, wait));
    }

    /** True while headphones (Bluetooth, wired or USB) are connected, which is where Android sends media then. */
    static boolean onHeadphones(AudioManager audio) {
        for (AudioDeviceInfo d : audio.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
            int t = d.getType();
            if (t == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || t == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || t == AudioDeviceInfo.TYPE_WIRED_HEADSET || t == AudioDeviceInfo.TYPE_USB_HEADSET) return true;
            if (Build.VERSION.SDK_INT >= 31 && t == AudioDeviceInfo.TYPE_BLE_HEADSET) return true;
        }
        return false;
    }

    static int minuteNow() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    }
}
