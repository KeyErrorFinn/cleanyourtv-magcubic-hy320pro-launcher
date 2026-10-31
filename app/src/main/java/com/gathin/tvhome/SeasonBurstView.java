package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;

/**
 * The seasonal skins' greeting for the first Home of the day: a transparent full-screen overlay that plays a
 * one-off fall of snow, confetti, shamrocks or Easter blossoms, or a drift of hearts rising from the bottom, then
 * runs a callback on the main thread. Like {@link PetalShowerView} it takes no focus, clicks or accessibility,
 * finishes within the duration it is given, and allocates nothing per frame. Halloween has its own
 * ({@link BatFlightView}).
 */
public final class SeasonBurstView extends View {

    static final int SNOW = 0, CONFETTI = 1, HEARTS = 2, CLOVERS = 3, BLOSSOMS = 4;

    private static final double TAU = 2 * Math.PI;
    private static final int[][] COLORS = {
            {0xFFFFFFFF, 0xFFE3F0FF, 0xFFC5DDFF},
            {0xFFFFD34D, 0xFFFF4D79, 0xFF4DA3FF, 0xFF3DDC84, 0xFFC79BFF, 0xFFFFFFFF},
            {0xFFFF4D79, 0xFFFF7A9C, 0xFFE5284E, 0xFFFFA6BE},
            {0xFF3DDC84, 0xFF2FBF6E, 0xFF7BE8A9, 0xFF1FA55C},
            {0xFFFFB3D9, 0xFFC79BFF, 0xFFFFE08A, 0xFFA8D8FF, 0xFFFFFFFF}};
    /** Smallest and extra size in dp, side-to-side sway in dp, spin in degrees per second. */
    private static final float[] MIN_SIZE = {5f, 12f, 14f, 15f, 13f};
    private static final float[] EXTRA_SIZE = {8f, 8f, 12f, 11f, 9f};
    private static final float[] SWAY = {14f, 8f, 18f, 16f, 20f};
    private static final float[] SPIN = {20f, 260f, 30f, 70f, 90f};

    private final float density;
    private final Random rnd = new Random();
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path heart = heartShape();
    private final Path clover = cloverShape();
    private final Path petals = petalsShape();
    private final RectF confetti = new RectF(-0.5f, -0.22f, 0.5f, 0.22f);

    private int style, count;
    private float[] xFrac = new float[0], delay = new float[0], life = new float[0], travel = new float[0],
            size = new float[0], swayAmp = new float[0], swayFreq = new float[0], swayPhase = new float[0],
            rot0 = new float[0], spin = new float[0], flipFreq = new float[0], flipPhase = new float[0];
    private int[] color = new int[0], shape = new int[0];

    private boolean playing;
    private long start, duration;
    private Runnable onDone;
    private final Runnable finish = this::finish;

    public SeasonBurstView(Context context) {
        super(context);
        density = context.getResources().getDisplayMetrics().density;
        setFocusable(false);
        setClickable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        setWillNotDraw(false);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(0.11f);
        stroke.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * Plays {@code particles} of {@code kind} ({@link #SNOW} and the others) over {@code durationMs}, then runs
     * {@code done} on the main thread. Playing again before the last one ends finishes that one first.
     */
    public void play(int kind, int particles, long durationMs, Runnable done) {
        if (playing) {
            removeCallbacks(finish);
            finish();
        }
        style = Math.max(0, Math.min(COLORS.length - 1, kind));
        int n = Math.max(0, particles);
        ensureCapacity(n);
        count = n;
        duration = Math.max(1, durationMs);
        onDone = done;
        for (int i = 0; i < n; i++) {
            xFrac[i] = (i + rnd.nextFloat()) / n;
            delay[i] = rnd.nextFloat() * 0.32f;
            life[i] = 0.6f + rnd.nextFloat() * (1f - delay[i] - 0.6f);
            travel[i] = 0.85f + rnd.nextFloat() * 0.3f;     // fraction of the height covered over its life
            size[i] = MIN_SIZE[style] + rnd.nextFloat() * EXTRA_SIZE[style];
            swayAmp[i] = SWAY[style] * (0.4f + rnd.nextFloat());
            swayFreq[i] = 0.4f + rnd.nextFloat() * 0.6f;
            swayPhase[i] = rnd.nextFloat() * (float) TAU;
            rot0[i] = rnd.nextFloat() * 360f;
            spin[i] = SPIN[style] * (0.4f + rnd.nextFloat()) * (rnd.nextBoolean() ? 1f : -1f);
            flipFreq[i] = 0.8f + rnd.nextFloat() * 1.6f;
            flipPhase[i] = rnd.nextFloat() * (float) TAU;
            color[i] = rnd.nextInt(COLORS[style].length);
            shape[i] = rnd.nextInt(2);
        }
        playing = true;
        start = SystemClock.uptimeMillis();
        postDelayed(finish, duration);
        postInvalidateOnAnimation();
    }

    private void finish() {
        if (!playing) return;
        playing = false;
        invalidate();
        Runnable done = onDone;
        onDone = null;
        if (done != null) done.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (playing) {
            removeCallbacks(finish);
            finish();
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        if (!playing) return;
        long now = SystemClock.uptimeMillis();
        float elapsed = (now - start) / (float) duration;
        float secs = (now - start) / 1000f;
        float w = getWidth(), h = getHeight();
        boolean rises = style == HEARTS;
        for (int i = 0; i < count; i++) {
            float p = (elapsed - delay[i]) / life[i];
            if (p <= 0f || p >= 1f) continue;
            float len = size[i] * density;
            // snow and hearts move at an even pace; the rest gather speed as they fall
            float moved = style == SNOW || rises ? p : (p + p * p) * 0.5f;
            float span = h * travel[i] + len * 3f;
            float y = rises ? h + len * 1.5f - span * moved : -len * 1.5f + span * moved;
            double sway = TAU * swayFreq[i] * secs + swayPhase[i];
            float x = xFrac[i] * w + swayAmp[i] * density * (float) Math.sin(sway);
            float angle = rot0[i] + spin[i] * secs;
            if (style == HEARTS) angle = 14f * (float) Math.sin(sway);   // hearts stay upright, tilting as they sway
            float flip = (float) Math.cos(TAU * flipFreq[i] * secs + flipPhase[i]);
            float sy = style == CONFETTI ? (Math.abs(flip) < 0.1f ? 0.1f : Math.abs(flip)) : 1f;
            float fade = p > 0.75f ? 1f - (p - 0.75f) / 0.25f : p < 0.1f ? p / 0.1f : 1f;
            int base = COLORS[style][color[i]];
            fill.setColor(base);
            fill.setAlpha(Math.round((style == SNOW ? 235 : 255) * fade));
            stroke.setColor(base);
            stroke.setAlpha(fill.getAlpha());

            c.save();
            c.translate(x, y);
            c.rotate(angle);
            c.scale(len, len * sy);
            drawShape(c, shape[i]);
            c.restore();
        }
        postInvalidateOnAnimation();
    }

    private void drawShape(Canvas c, int variant) {
        switch (style) {
            case SNOW:
                if (variant == 0) {
                    c.drawCircle(0f, 0f, 0.4f, fill);
                } else {
                    // a six-armed flake: three lines through the centre
                    c.drawLine(-0.5f, 0f, 0.5f, 0f, stroke);
                    c.rotate(60f);
                    c.drawLine(-0.5f, 0f, 0.5f, 0f, stroke);
                    c.rotate(60f);
                    c.drawLine(-0.5f, 0f, 0.5f, 0f, stroke);
                }
                break;
            case CONFETTI:
                if (variant == 0) c.drawRect(confetti, fill);
                else c.drawCircle(0f, 0f, 0.26f, fill);
                break;
            case HEARTS:
                c.drawPath(heart, fill);
                break;
            case CLOVERS:
                c.drawPath(clover, fill);
                break;
            default:
                c.drawPath(petals, fill);
                // a pale centre so it reads as a flower, not a spinner
                int alpha = fill.getAlpha();
                fill.setColor(0xFFFFD34D);
                fill.setAlpha(alpha);
                c.drawCircle(0f, 0f, 0.13f, fill);
                break;
        }
    }

    private void ensureCapacity(int n) {
        if (xFrac.length >= n) return;
        xFrac = new float[n];
        delay = new float[n];
        life = new float[n];
        travel = new float[n];
        size = new float[n];
        swayAmp = new float[n];
        swayFreq = new float[n];
        swayPhase = new float[n];
        rot0 = new float[n];
        spin = new float[n];
        flipFreq = new float[n];
        flipPhase = new float[n];
        color = new int[n];
        shape = new int[n];
    }

    /** A heart about one unit wide, centred on the origin. */
    private static Path heartShape() {
        Path p = new Path();
        p.moveTo(0f, 0.42f);
        p.cubicTo(-0.62f, 0.02f, -0.52f, -0.46f, 0f, -0.16f);
        p.cubicTo(0.52f, -0.46f, 0.62f, 0.02f, 0f, 0.42f);
        p.close();
        return p;
    }

    /** A four-leaf shamrock: four round lobes and a short stem. */
    private static Path cloverShape() {
        Path p = new Path();
        p.addCircle(-0.2f, -0.2f, 0.21f, Path.Direction.CW);
        p.addCircle(0.2f, -0.2f, 0.21f, Path.Direction.CW);
        p.addCircle(-0.2f, 0.2f, 0.21f, Path.Direction.CW);
        p.addCircle(0.2f, 0.2f, 0.21f, Path.Direction.CW);
        p.addRect(-0.04f, 0.1f, 0.04f, 0.58f, Path.Direction.CW);
        return p;
    }

    /** Five rounded petals around the origin. */
    private static Path petalsShape() {
        Path p = new Path();
        Path one = new Path();
        one.addOval(new RectF(-0.15f, -0.5f, 0.15f, -0.02f), Path.Direction.CW);
        Matrix m = new Matrix();
        for (int k = 0; k < 5; k++) {
            Path rotated = new Path();
            m.setRotate(k * 72f);
            one.transform(m, rotated);
            p.addPath(rotated);
        }
        return p;
    }
}
