package com.gathin.tvhome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A seasonal pill icon from a picture (Microsoft's Fluent Emoji 3D, MIT) with one short motion played each time
 * the pill gains focus: a bat's two wing beats, a ghost floating up, a hop, a wobble or a heartbeat. The motion is
 * a scale, offset or tilt of the same picture, played once per {@link #nudge()}; reduced motion leaves it still.
 */
public final class SeasonIconDrawable extends Drawable {

    /** Two wing beats, the picture squeezing vertically. */
    static final int FLAP = 0;
    /** Drifts up and settles, tilting slightly. */
    static final int FLOAT = 1;
    /** Two small hops. */
    static final int HOP = 2;
    /** A damped side-to-side tilt, like a bell or a leaf. */
    static final int WOBBLE = 3;
    /** A double heartbeat. */
    static final int PULSE = 4;

    private static final float BOX_DP = 24f;
    private static final long[] DURATION_MS = {520, 900, 620, 700, 680};

    private final int motion;
    private final float density;
    private final Bitmap bitmap;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Rect dst = new Rect();
    private final ValueAnimator animator;
    private float t = -1f;
    private boolean reduced;
    private int alpha = 255;

    public SeasonIconDrawable(Resources res, float density, @DrawableRes int picture, int motion) {
        this.motion = motion;
        this.density = density;
        bitmap = BitmapFactory.decodeResource(res, picture);
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(DURATION_MS[motion]);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            t = (float) a.getAnimatedValue();
            invalidateSelf();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator a) {
                t = -1f;
                invalidateSelf();
            }
        });
    }

    /** The focus motion. */
    public void nudge() {
        if (reduced) return;
        animator.cancel();
        animator.start();
    }

    /** With reduced motion {@link #nudge()} does nothing and a motion in progress stops at rest. */
    public void setReducedMotion(boolean reducedMotion) {
        reduced = reducedMotion;
        if (reduced && animator.isRunning()) animator.cancel();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect b = getBounds();
        float sx = 1f, sy = 1f, dy = 0f, rot = 0f;
        if (t >= 0f) {
            float wave = (float) Math.sin(t * 2 * Math.PI * 2);
            switch (motion) {
                case FLAP:
                    // the wings fold (the picture squeezes vertically) and open again, twice
                    sy = 1f - 0.28f * Math.abs(wave);
                    sx = 1f + 0.06f * Math.abs(wave);
                    dy = -1.5f * density * (float) Math.sin(t * Math.PI);
                    break;
                case FLOAT:
                    // drifts up about 3 dp, tilting slightly, then sinks back
                    dy = -3f * density * (float) Math.sin(t * Math.PI);
                    rot = 6f * (float) Math.sin(t * 2 * Math.PI) * (1f - t);
                    break;
                case HOP:
                    dy = -3.5f * density * Math.abs(wave) * (1f - 0.4f * t);
                    break;
                case WOBBLE:
                    rot = 14f * wave * (1f - t);
                    break;
                default:
                    sx = sy = 1f + 0.18f * Math.max(0f, wave) * (1f - 0.4f * t);
                    break;
            }
        }
        int save = canvas.save();
        canvas.translate(b.exactCenterX(), b.exactCenterY() + dy);
        canvas.rotate(rot);
        canvas.scale(sx, sy);
        int hw = b.width() / 2, hh = b.height() / 2;
        dst.set(-hw, -hh, b.width() - hw, b.height() - hh);
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap, null, dst, paint);
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
        paint.setColorFilter(filter);
        invalidateSelf();
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
