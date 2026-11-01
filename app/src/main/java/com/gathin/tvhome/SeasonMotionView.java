package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;

/**
 * The slow movement over a {@link SeasonBackdropView}: snow, rising hearts, drifting petals, embers and bats,
 * fireworks, twinkling stars. A transparent full-screen overlay that takes no focus or clicks and draws at about
 * 30 frames a second, only while its window is on screen. It keeps to the same clear middle of Home as the scenes
 * do, and its particles are small and soft so the clock, greeting and dock stay readable.
 */
public final class SeasonMotionView extends View {

    private static final double TAU = 2 * Math.PI;
    private static final long FRAME_MS = 33;
    private static final int DOT = 0, FLAKE = 1, HEART = 2, CLOVER = 3, PETAL = 4, EMBER = 5;
    private static final int SPARKS = 26;
    private static final long FIRE_PERIOD = 3600L;
    private static final int FIRE_SHOTS = 3;
    private static final int[] FIRE_HUES = {0xFFFFD34D, 0xFFFF4D79, 0xFF4DA3FF, 0xFF3DDC84, 0xFFC79BFF};

    private final Season season;
    private final float density;
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path heart = SeasonBurstView.heartShape();
    private final Path clover = SeasonBurstView.cloverShape();
    private final Path petals = SeasonBurstView.petalsShape();
    private final Path wing = new Path();
    private final RectF oval = new RectF();
    private final Random fireRandom = new Random();

    // drifting particles: a fall (or rise) down the screen, with a sway
    private int count;
    private float[] x, phase, speed, size, sway, swayHz, swayOff, spin, alphaScale;
    private int[] color;
    private int[] palette;
    private int kind;
    private boolean rises;
    private float wind;
    // twinkling points
    private float[] tx, ty, tHz, tOff, tSize;
    private int tint;

    private boolean running;
    private final Runnable tick = this::invalidate;

    public SeasonMotionView(Context context, Season season) {
        super(context);
        this.season = season;
        density = context.getResources().getDisplayMetrics().density;
        setFocusable(false);
        setClickable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeCap(Paint.Cap.ROUND);
        configure();
    }

    private void configure() {
        Random r = new Random(season.ordinal() * 104729L + 5);
        switch (season) {
            case CHRISTMAS:
                drift(r, 55, FLAKE, new int[] {0xFFFFFFFF, 0xFFE3F0FF, 0xFFC5DDFF}, 0.05f, 0.11f, 3f, 7f, 14f, false, -0.01f);
                twinkles(r, 14, 0xFFFFF2C4, 0.04f, 0.5f);
                break;
            case VALENTINES:
                drift(r, 22, HEART, new int[] {0xFFFF4D79, 0xFFFF7A9C, 0xFFE5284E, 0xFFFFA6BE}, 0.04f, 0.08f, 10f, 20f, 16f, true, 0f);
                twinkles(r, 16, 0xFFFFD3E0, 0.05f, 0.7f);
                break;
            case ST_PATRICKS:
                drift(r, 16, CLOVER, new int[] {0xFF3DDC84, 0xFF2FBF6E, 0xFF7BE8A9, 0xFF1FA55C}, 0.035f, 0.07f, 12f, 20f, 18f, false, 0.01f);
                twinkles(r, 18, 0xFFFFD34D, 0.30f, 0.66f);
                break;
            case EASTER:
                drift(r, 16, PETAL, new int[] {0xFFFFB3D9, 0xFFC79BFF, 0xFFFFE08A, 0xFFA8D8FF, 0xFFFFFFFF}, 0.035f, 0.07f, 11f, 18f, 22f, false, 0.015f);
                break;
            case HALLOWEEN:
                drift(r, 22, EMBER, new int[] {0xFFFF7A1A, 0xFFFFB347, 0xFFFFD34D}, 0.03f, 0.07f, 2.5f, 5f, 12f, true, 0f);
                twinkles(r, 20, 0xFFFFFFFF, 0.04f, 0.5f);
                break;
            default:
                twinkles(r, 28, 0xFFFFE9A8, 0.04f, 0.55f);
                break;
        }
    }

    private void drift(Random r, int n, int shape, int[] colors, float minSpeed, float maxSpeed, float minSize,
                       float maxSize, float swayDp, boolean up, float windPerSecond) {
        count = n;
        kind = shape;
        palette = colors;
        rises = up;
        wind = windPerSecond;
        x = new float[n]; phase = new float[n]; speed = new float[n]; size = new float[n]; sway = new float[n];
        swayHz = new float[n]; swayOff = new float[n]; spin = new float[n]; alphaScale = new float[n]; color = new int[n];
        for (int i = 0; i < n; i++) {
            x[i] = r.nextFloat();
            phase[i] = r.nextFloat();
            speed[i] = minSpeed + r.nextFloat() * (maxSpeed - minSpeed);
            size[i] = minSize + r.nextFloat() * (maxSize - minSize);
            sway[i] = swayDp * (0.4f + r.nextFloat());
            swayHz[i] = 0.12f + r.nextFloat() * 0.25f;
            swayOff[i] = r.nextFloat() * (float) TAU;
            spin[i] = (r.nextBoolean() ? 1f : -1f) * (20f + r.nextFloat() * 50f);
            alphaScale[i] = 0.55f + r.nextFloat() * 0.45f;
            color[i] = r.nextInt(colors.length);
        }
    }

    private void twinkles(Random r, int n, int colour, float top, float bottom) {
        tx = new float[n]; ty = new float[n]; tHz = new float[n]; tOff = new float[n]; tSize = new float[n];
        tint = colour;
        for (int i = 0; i < n; i++) {
            tx[i] = 0.05f + r.nextFloat() * 0.9f;
            ty[i] = top + r.nextFloat() * (bottom - top);
            tHz[i] = 0.15f + r.nextFloat() * 0.35f;
            tOff[i] = r.nextFloat() * (float) TAU;
            tSize[i] = 1.2f + r.nextFloat() * 2.2f;
        }
    }

    // ---- lifecycle --------------------------------------------------------------------------------

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        running = getWindowVisibility() == VISIBLE;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        running = false;
        removeCallbacks(tick);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        running = visibility == VISIBLE;
        removeCallbacks(tick);
        if (running) invalidate();
    }

    // ---- drawing ----------------------------------------------------------------------------------

    @Override
    protected void onDraw(Canvas c) {
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0 || !running) return;
        long now = SystemClock.uptimeMillis();
        float t = now / 1000f;
        if (tx != null) twinkle(c, w, h, t);
        if (count > 0) drifters(c, w, h, t);
        if (season == Season.NEW_YEAR) fireworks(c, w, h, now);
        if (season == Season.HALLOWEEN) bats(c, w, h, now);
        if (season == Season.EASTER) clouds(c, w, h, t);
        postDelayed(tick, FRAME_MS);
    }

    private void twinkle(Canvas c, float w, float h, float t) {
        for (int i = 0; i < tx.length; i++) {
            float pulse = 0.5f + 0.5f * (float) Math.sin(TAU * tHz[i] * t + tOff[i]);
            fill.setColor(tint);
            fill.setAlpha(Math.round(40 + 190 * pulse * pulse));
            c.drawCircle(tx[i] * w, ty[i] * h, tSize[i] * density * (0.6f + 0.6f * pulse), fill);
        }
    }

    private void drifters(Canvas c, float w, float h, float t) {
        for (int i = 0; i < count; i++) {
            float len = size[i] * density;
            float cycle = 1f + 3f * len / h;
            float p = (phase[i] + t * speed[i]) % cycle / cycle;          // 0..1 along the whole trip
            float trip = h + len * 3f;
            float y = rises ? h + len * 1.5f - p * trip : -len * 1.5f + p * trip;
            float px = (x[i] + wind * t) % 1f;
            if (px < 0) px += 1f;
            double s = TAU * swayHz[i] * t + swayOff[i];
            float xx = px * w + sway[i] * density * (float) Math.sin(s);
            float fade = p < 0.08f ? p / 0.08f : p > 0.8f ? (1f - p) / 0.2f : 1f;
            int alpha = Math.round((kind == FLAKE ? 215 : 190) * fade * alphaScale[i]);
            if (kind == EMBER) alpha = Math.round(alpha * (0.6f + 0.4f * (float) Math.sin(s * 3)));
            fill.setColor(palette[color[i]]);
            fill.setAlpha(Math.max(0, alpha));
            if (kind == FLAKE || kind == DOT || kind == EMBER) {
                c.drawCircle(xx, y, len * 0.5f, fill);
                continue;
            }
            c.save();
            c.translate(xx, y);
            c.rotate(kind == HEART ? 14f * (float) Math.sin(s) : spin[i] * t);
            c.scale(len, len);
            c.drawPath(kind == HEART ? heart : kind == CLOVER ? clover : petals, fill);
            c.restore();
        }
    }

    /** Three bursts at a time over the clear middle of the sky, each a ring of sparks that arc down and fade. */
    private void fireworks(Canvas c, float w, float h, long now) {
        for (int shot = 0; shot < FIRE_SHOTS; shot++) {
            long shifted = now + shot * (FIRE_PERIOD / FIRE_SHOTS);
            long cycle = shifted / FIRE_PERIOD;
            float age = (shifted % FIRE_PERIOD) / 1000f;                    // seconds since this one was fired
            if (age > 2.4f) continue;
            fireRandom.setSeed(cycle * 31L + shot * 977L + 3);
            float cx = (0.45f + fireRandom.nextFloat() * 0.45f) * w;
            float cy = (0.22f + fireRandom.nextFloat() * 0.26f) * h;
            int hue = FIRE_HUES[fireRandom.nextInt(FIRE_HUES.length)];
            float reach = (0.10f + fireRandom.nextFloat() * 0.06f) * h;
            if (age < 0.45f) {                                               // the shell climbs first
                fill.setColor(0xFFFFE9A8);
                fill.setAlpha(220);
                c.drawCircle(cx, cy + (1f - age / 0.45f) * 0.22f * h, 2f * density, fill);
                continue;
            }
            float a = age - 0.45f;
            float out = 1f - (float) Math.pow(1f - Math.min(1f, a / 1.2f), 2);
            float fade = Math.max(0f, 1f - a / 1.95f);
            line.setStrokeWidth(2.2f * density);
            line.setColor(hue);
            fill.setColor(hue);
            line.setAlpha(Math.round(200 * fade));
            fill.setAlpha(Math.round(235 * fade));
            float tail = 0.018f * h * (1f - out * 0.6f);
            for (int k = 0; k < SPARKS; k++) {
                double ang = TAU * k / SPARKS;
                float dist = reach * out * (0.85f + 0.15f * (k % 3));
                float cos = (float) Math.cos(ang), sin = (float) Math.sin(ang);
                float sx = cx + cos * dist;
                float sy = cy + sin * dist + 14f * density * a * a;
                c.drawLine(sx, sy, sx - cos * tail, sy - sin * tail, line);
                c.drawCircle(sx, sy, 1.8f * density, fill);
            }
        }
    }

    /** Three bats crossing the night sky one after another, wings beating. */
    private void bats(Canvas c, float w, float h, long now) {
        for (int i = 0; i < 3; i++) {
            long period = 16000L + i * 3000L;
            float p = ((now + i * 5200L) % period) / (float) period;
            if (p > 0.55f) continue;                                         // on screen for just over half a cycle
            float q = p / 0.55f;
            boolean right = i % 2 == 0;
            float bx = (right ? -0.06f + 1.12f * q : 1.06f - 1.12f * q) * w;
            float by = (0.26f + 0.10f * i + 0.04f * (float) Math.sin(q * 9 + i)) * h;
            float flap = (float) Math.sin(now / 1000f * 14f + i);
            float s = (22f + 6f * i) * density;
            fill.setColor(0xFF0B0614);
            fill.setAlpha(235);
            c.save();
            c.translate(bx, by);
            c.scale(right ? 1f : -1f, 1f);
            wing.rewind();
            for (int side = -1; side <= 1; side += 2) {
                wing.moveTo(0, 0);
                wing.quadTo(side * s * 0.5f, -s * (0.5f + 0.5f * flap), side * s, -s * 0.15f * flap);
                wing.quadTo(side * s * 0.62f, s * 0.22f, side * s * 0.4f, s * 0.12f);
                wing.quadTo(side * s * 0.22f, s * 0.28f, 0, s * 0.18f);
                wing.close();
            }
            c.drawPath(wing, fill);
            oval.set(-s * 0.1f, -s * 0.12f, s * 0.1f, s * 0.22f);
            c.drawOval(oval, fill);
            c.restore();
        }
    }

    /** Soft white clouds sliding across the upper sky. */
    private void clouds(Canvas c, float w, float h, float t) {
        for (int i = 0; i < 3; i++) {
            float span = w * 1.5f;
            float cx = ((t * (14f + i * 6f) * density + i * span / 3f) % span) - w * 0.25f;
            float cy = (0.30f + 0.09f * i) * h;
            float s = (46f + 10f * i) * density;
            fill.setColor(0xFFFFFFFF);
            fill.setAlpha(70);
            oval.set(cx - s * 1.2f, cy - s * 0.30f, cx + s * 1.2f, cy + s * 0.34f);
            c.drawOval(oval, fill);
            oval.set(cx - s * 0.6f, cy - s * 0.62f, cx + s * 0.55f, cy + s * 0.16f);
            c.drawOval(oval, fill);
        }
    }
}
