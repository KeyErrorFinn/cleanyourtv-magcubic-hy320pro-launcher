package com.gathin.tvhome;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A seasonal picture for the network pill that follows the connection like the diya and the jack-o'-lantern do:
 * full colour while online, a muted grey one when the connection drops. Its colour strength follows the signal
 * level (1 to 4) and it breathes a little while started; Ethernet holds steady. The picture is drawn twice, grey
 * and in colour, and the colour is cross-faded over the grey. Frames run at about 15 fps only while the picture
 * breathes or fades; nothing is allocated per frame.
 */
public final class SeasonLampDrawable extends Drawable implements FestiveLamp {

    private static final float BOX_DP = 24f;
    private static final long FRAME_MS = 66, FADE_MS = 350;
    /** How strongly the colour shows at each level; index 0 is the grey picture alone. */
    private static final float[] LEVEL_COLOUR = {0f, 0.55f, 0.72f, 0.88f, 1f};
    private static final ColorFilter GREY = greyFilter();

    private final float density;
    private final Bitmap bitmap;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Rect dst = new Rect();
    private final long epoch = SystemClock.uptimeMillis();

    private int level = 4;
    private boolean primed, running, steady, reduced;
    private float from = 1f, to = 1f;
    private long fadeStart;
    private int alpha = 255;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
            if (wantsFrames()) scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_MS);
        }
    };

    public SeasonLampDrawable(Resources res, float density, @DrawableRes int picture) {
        this.density = density;
        bitmap = BitmapFactory.decodeResource(res, picture);
    }

    private static ColorFilter greyFilter() {
        ColorMatrix m = new ColorMatrix();
        m.setSaturation(0.12f);
        m.postConcat(new ColorMatrix(new float[] {
                0.78f, 0, 0, 0, 0,
                0, 0.78f, 0, 0, 0,
                0, 0, 0.78f, 0, 0,
                0, 0, 0, 0.8f, 0}));
        return new ColorMatrixColorFilter(m);
    }

    @Override
    public void setFlame(int newLevel) {
        int lvl = Math.max(0, Math.min(4, newLevel));
        long now = SystemClock.uptimeMillis();
        if (!primed || reduced) {
            primed = true;
            level = lvl;
            from = to = LEVEL_COLOUR[lvl];
            kick();
            return;
        }
        if (lvl == level) return;
        from = colour(now);
        to = LEVEL_COLOUR[lvl];
        fadeStart = now;
        level = lvl;
        kick();
    }

    @Override
    public void setSteady(boolean steady) {
        if (this.steady == steady) return;
        this.steady = steady;
        kick();
    }

    @Override
    public void setReducedMotion(boolean reducedMotion) {
        if (reduced == reducedMotion) return;
        reduced = reducedMotion;
        if (reduced) from = to;
        kick();
    }

    @Override
    public void start() {
        if (running) return;
        running = true;
        kick();
    }

    @Override
    public void stop() {
        if (!running) return;
        running = false;
        from = to;
        kick();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (changed) kick();
        return changed;
    }

    private boolean fading(long now) {
        return from != to && now - fadeStart < FADE_MS;
    }

    private boolean breathing() {
        return running && !reduced && !steady && level > 0;
    }

    private boolean wantsFrames() {
        return isVisible() && (breathing() || (running && !reduced && fading(SystemClock.uptimeMillis())));
    }

    private void kick() {
        unscheduleSelf(tick);
        if (wantsFrames()) scheduleSelf(tick, SystemClock.uptimeMillis() + FRAME_MS);
        invalidateSelf();
    }

    /** The colour strength now, easing from the last level to the new one. */
    private float colour(long now) {
        float p = Math.min(1f, Math.max(0f, (now - fadeStart) / (float) FADE_MS));
        float e = 1f - (1f - p) * (1f - p);
        return from + (to - from) * e;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        Rect b = getBounds();
        float scale = breathing() ? 1f + 0.04f * (float) Math.sin((now - epoch) / 1000f * 2.6f) : 1f;
        int save = canvas.save();
        canvas.scale(scale, scale, b.exactCenterX(), b.exactCenterY());
        dst.set(b);
        paint.setColorFilter(GREY);
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap, null, dst, paint);
        float c = colour(now);
        if (c > 0.01f) {
            paint.setColorFilter(null);
            paint.setAlpha(Math.round(alpha * Math.min(1f, c)));
            canvas.drawBitmap(bitmap, null, dst, paint);
        }
        canvas.restoreToCount(save);
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.round(BOX_DP * density);
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.round(BOX_DP * density);
    }

    @Override
    public void setAlpha(int alpha) {
        if (this.alpha == alpha) return;
        this.alpha = alpha;
        invalidateSelf();
    }

    @Override
    public int getAlpha() {
        return alpha;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter filter) {
        // The picture keeps its own colours; the pills' focus tint is switched off for festive icons.
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
