package com.gathin.tvhome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import java.util.Random;

/**
 * The seasonal wallpaper: a scene for each {@link Season} drawn from gradients and simple shapes, with no picture
 * files (so nothing to license or decode) and no bitmap of its own. It is static, drawn once into a hardware layer,
 * and ends with a shade over the lower part and the top left so the clock, greeting and dock stay readable. The
 * scenes are seeded, so a season always looks the same.
 */
public final class SeasonBackdropView extends View {

    private final Season season;
    private final float density;
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF oval = new RectF();
    private final Random rnd = new Random();

    public SeasonBackdropView(Context context, Season season) {
        super(context);
        this.season = season;
        density = context.getResources().getDisplayMetrics().density;
        setFocusable(false);
        setClickable(false);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        // One texture, composited each frame, instead of replaying a few hundred shapes under every animation.
        setLayerType(LAYER_TYPE_HARDWARE, null);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeCap(Paint.Cap.ROUND);
        stroke.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(Canvas c) {
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        rnd.setSeed(season.ordinal() * 7919L + 11);
        switch (season) {
            case HALLOWEEN:
                halloween(c, w, h);
                break;
            case CHRISTMAS:
                christmas(c, w, h);
                break;
            case NEW_YEAR:
                newYear(c, w, h);
                break;
            case EASTER:
                easter(c, w, h);
                break;
            case VALENTINES:
                valentines(c, w, h);
                break;
            default:
                stPatricks(c, w, h);
                break;
        }
        shade(c, w, h);
    }

    // ---- scenes ------------------------------------------------------------------------------------

    private void halloween(Canvas c, float w, float h) {
        sky(c, w, h, 0xFF120824, 0xFF2A1240, 0xFF1A0B26);
        stars(c, w, h, 110, 0.6f, 0xFFFFFFFF);
        float mx = 0.66f * w, my = 0.40f * h, mr = 0.10f * h;
        glow(c, mx, my, mr * 3.4f, 0x66F5E6A8);
        fill.setColor(0xFFF5E6A8);
        c.drawCircle(mx, my, mr, fill);
        fill.setColor(0x26B89A4A);
        c.drawCircle(mx - mr * 0.3f, my - mr * 0.25f, mr * 0.22f, fill);
        c.drawCircle(mx + mr * 0.35f, my + mr * 0.2f, mr * 0.3f, fill);
        c.drawCircle(mx + mr * 0.05f, my + mr * 0.62f, mr * 0.14f, fill);
        fill.setColor(0xCC2B1646);
        cloud(c, mx - mr * 1.6f, my + mr * 0.4f, mr * 2.4f, mr * 0.34f);
        cloud(c, mx + mr * 0.2f, my - mr * 0.5f, mr * 2.0f, mr * 0.26f);
        hills(c, w, h, 0.70f * h, 0.04f * h, 0xFF1A0F2B);
        // the house on the hill, lit windows
        float x0 = 0.74f * w, base = 0.72f * h, bw = 0.14f * w, bh = 0.12f * h;
        fill.setColor(0xFF0D0716);
        c.drawRect(x0, base - bh, x0 + bw, base + 0.02f * h, fill);
        triangle(c, x0 - 0.012f * w, base - bh, x0 + bw * 0.5f, base - bh - 0.1f * h, x0 + bw + 0.012f * w, base - bh);
        float tx = x0 + bw * 0.74f, tw = 0.045f * w;
        c.drawRect(tx, base - bh - 0.1f * h, tx + tw, base - bh, fill);
        triangle(c, tx - 0.008f * w, base - bh - 0.1f * h, tx + tw * 0.5f, base - bh - 0.2f * h, tx + tw + 0.008f * w, base - bh - 0.1f * h);
        fill.setColor(0xFFE8A33C);
        float ww = 0.018f * w, wh = 0.034f * h;
        c.drawRect(x0 + 0.02f * w, base - bh * 0.8f, x0 + 0.02f * w + ww, base - bh * 0.8f + wh, fill);
        c.drawRect(x0 + 0.06f * w, base - bh * 0.8f, x0 + 0.06f * w + ww, base - bh * 0.8f + wh, fill);
        c.drawRect(x0 + 0.02f * w, base - bh * 0.4f, x0 + 0.02f * w + ww, base - bh * 0.4f + wh, fill);
        c.drawRect(tx + tw * 0.3f, base - bh - 0.07f * h, tx + tw * 0.3f + ww * 0.8f, base - bh - 0.07f * h + wh, fill);
        c.drawRect(x0 + bw * 0.5f - ww * 0.5f, base - 0.05f * h, x0 + bw * 0.5f + ww * 0.5f, base + 0.02f * h, fill);
        hills(c, w, h, 0.80f * h, 0.03f * h, 0xFF0C0715);
        stroke.setColor(0xFF08050F);
        tree(c, 0.07f * w, 0.84f * h, 0.10f * h, -90f, 8, 1);
        tree(c, 0.985f * w, 0.84f * h, 0.075f * h, -92f, 8, 2);
        // pumpkins in the grass
        pumpkin(c, 0.30f * w, 0.945f * h, 0.035f * h);
        pumpkin(c, 0.36f * w, 0.96f * h, 0.026f * h);
        pumpkin(c, 0.58f * w, 0.95f * h, 0.03f * h);
    }

    private void christmas(Canvas c, float w, float h) {
        sky(c, w, h, 0xFF07182E, 0xFF0F3558, 0xFF1D5F7A);
        stars(c, w, h, 130, 0.58f, 0xFFFFFFFF);
        float sx = 0.70f * w, sy = 0.34f * h;
        glow(c, sx, sy, 0.16f * h, 0x88FFE9A0);
        fill.setColor(0xFFFFF3C4);
        fourPointStar(c, sx, sy, 0.07f * h);
        hills(c, w, h, 0.72f * h, 0.04f * h, 0xFFB5CDE6);
        float[] xs = {0.05f, 0.14f, 0.24f, 0.35f, 0.55f, 0.66f, 0.80f, 0.90f, 0.97f};
        float[] hs = {0.2f, 0.14f, 0.17f, 0.12f, 0.13f, 0.19f, 0.15f, 0.21f, 0.13f};
        for (int i = 0; i < xs.length; i++) pine(c, xs[i] * w, 0.78f * h, hs[i] * 1.3f * h);
        hills(c, w, h, 0.82f * h, 0.03f * h, 0xFFD8E6F3);
        snow(c, w, h, 170);
    }

    private void newYear(Canvas c, float w, float h) {
        sky(c, w, h, 0xFF050818, 0xFF140C38, 0xFF3A1250);
        stars(c, w, h, 60, 0.5f, 0xFFFFFFFF);
        float[][] bursts = {{0.42f, 0.34f, 0.08f}, {0.60f, 0.32f, 0.10f}, {0.80f, 0.40f, 0.12f},
                {0.52f, 0.54f, 0.07f}, {0.93f, 0.56f, 0.06f}, {0.70f, 0.58f, 0.06f}};
        int[] colors = {0xFFFFD34D, 0xFFFF5C9A, 0xFF5CE1FF, 0xFFC79BFF, 0xFFFFD34D, 0xFFFF8A5C};
        for (int i = 0; i < bursts.length; i++) firework(c, bursts[i][0] * w, bursts[i][1] * h, bursts[i][2] * h, colors[i]);
        // city skyline with lit windows
        float x = 0;
        while (x < w) {
            float bw = (0.03f + rnd.nextFloat() * 0.035f) * w, bh = (0.12f + rnd.nextFloat() * 0.22f) * h;
            fill.setColor(0xFF0A0B1E);
            c.drawRect(x, h - bh, x + bw, h, fill);
            fill.setColor(0xCCFFD97A);
            for (float wy = h - bh + 0.012f * h; wy < h - 0.02f * h; wy += 0.028f * h) {
                for (float wx = x + 0.006f * w; wx < x + bw - 0.01f * w; wx += 0.013f * w) {
                    if (rnd.nextFloat() < 0.4f) c.drawRect(wx, wy, wx + 0.006f * w, wy + 0.012f * h, fill);
                }
            }
            x += bw + 0.002f * w;
        }
    }

    private void easter(Canvas c, float w, float h) {
        sky(c, w, h, 0xFF2D2B6E, 0xFF8E5FB5, 0xFFF4B0C6);
        glow(c, 0.68f * w, 0.38f * h, 0.3f * h, 0x66FFE9C4);
        fill.setColor(0x55FFFFFF);
        cloud(c, 0.46f * w, 0.30f * h, 0.14f * w, 0.03f * h);
        cloud(c, 0.64f * w, 0.46f * h, 0.15f * w, 0.032f * h);
        cloud(c, 0.84f * w, 0.34f * h, 0.12f * w, 0.03f * h);
        hills(c, w, h, 0.70f * h, 0.04f * h, 0xFF58A868);
        hills(c, w, h, 0.80f * h, 0.035f * h, 0xFF3F8A52);
        egg(c, 0.36f * w, 0.665f * h, 0.03f * w, 0.05f * h, 0xFFFFC9DE, 0xFFFFFFFF, 0);
        egg(c, 0.47f * w, 0.67f * h, 0.027f * w, 0.046f * h, 0xFFB7E3FF, 0xFFFFFFFF, 1);
        egg(c, 0.58f * w, 0.66f * h, 0.031f * w, 0.052f * h, 0xFFFFF0A8, 0xFFFF9EC4, 2);
        egg(c, 0.69f * w, 0.67f * h, 0.027f * w, 0.046f * h, 0xFFD9C2FF, 0xFFFFFFFF, 0);
        egg(c, 0.80f * w, 0.655f * h, 0.032f * w, 0.054f * h, 0xFFC4F2CF, 0xFFFFFFFF, 1);
        egg(c, 0.91f * w, 0.67f * h, 0.028f * w, 0.048f * h, 0xFFFFD4A8, 0xFFFFFFFF, 2);
        int[] petals = {0xFFFFFFFF, 0xFFFFE08A, 0xFFFFB3D9, 0xFFC79BFF};
        for (int i = 0; i < 16; i++) {
            float fx = rnd.nextFloat() * w, fy = (0.78f + rnd.nextFloat() * 0.19f) * h, r = (0.008f + rnd.nextFloat() * 0.008f) * h;
            fill.setColor(petals[i % petals.length]);
            for (int k = 0; k < 5; k++) {
                double a = k * 2 * Math.PI / 5;
                c.drawCircle(fx + (float) Math.cos(a) * r * 1.4f, fy + (float) Math.sin(a) * r * 1.4f, r, fill);
            }
            fill.setColor(0xFFFFC93C);
            c.drawCircle(fx, fy, r * 0.9f, fill);
        }
    }

    private void valentines(Canvas c, float w, float h) {
        sky(c, w, h, 0xFF2B0821, 0xFF63103F, 0xFFA3204F);
        for (int i = 0; i < 24; i++) {
            fill.setColor(((20 + rnd.nextInt(30)) << 24) | (rnd.nextBoolean() ? 0xFFB3D1 : 0xFFFFFF));
            c.drawCircle(rnd.nextFloat() * w, rnd.nextFloat() * h, (0.02f + rnd.nextFloat() * 0.06f) * h, fill);
        }
        float hx = 0.74f * w, hy = 0.42f * h;
        glow(c, hx, hy, 0.4f * h, 0x55FF6B9A);
        fill.setColor(0x33FF8FB5);
        heart(c, hx, hy, 0.34f * h, 0f);
        int[] tints = {0xFFB3D1, 0xFF7A9C, 0xFFFFFF, 0xFF4D79};
        for (int i = 0; i < 30; i++) {
            fill.setColor(((26 + rnd.nextInt(70)) << 24) | tints[rnd.nextInt(tints.length)]);
            heart(c, rnd.nextFloat() * w, rnd.nextFloat() * h, (0.025f + rnd.nextFloat() * 0.07f) * h, (rnd.nextFloat() - 0.5f) * 50f);
        }
    }

    private void stPatricks(Canvas c, float w, float h) {
        sky(c, w, h, 0xFF03170F, 0xFF0A4A2B, 0xFF1A7F45);
        // a rainbow rising from the hills on the right
        int[] bow = {0xFFFF5252, 0xFFFF9F43, 0xFFFFDD59, 0xFF4ADE80, 0xFF4DABF7, 0xFF7C6CF0, 0xFFC084FC};
        float cx = 0.70f * w, cy = 0.72f * h, band = 0.02f * h;
        stroke.setStrokeCap(Paint.Cap.BUTT);
        stroke.setStrokeWidth(band);
        for (int i = 0; i < bow.length; i++) {
            float r = 0.40f * h - i * band * 0.95f;
            stroke.setColor((bow[i] & 0xFFFFFF) | 0xA0000000);
            oval.set(cx - r, cy - r, cx + r, cy + r);
            c.drawArc(oval, 180f, 180f, false, stroke);
        }
        stroke.setStrokeCap(Paint.Cap.ROUND);
        fill.setColor(0x66FFFFFF);
        cloud(c, cx - 0.40f * h - 0.06f * w, cy - 0.03f * h, 0.12f * w, 0.035f * h);
        cloud(c, cx + 0.36f * h, cy - 0.03f * h, 0.12f * w, 0.035f * h);
        for (int i = 0; i < 26; i++) {
            fill.setColor(((22 + rnd.nextInt(40)) << 24) | 0x7BE8A9);
            clover(c, rnd.nextFloat() * w, rnd.nextFloat() * 0.8f * h, (0.03f + rnd.nextFloat() * 0.05f) * h, rnd.nextFloat() * 90f);
        }
        hills(c, w, h, 0.74f * h, 0.035f * h, 0xFF0B5A2E);
        hills(c, w, h, 0.83f * h, 0.03f * h, 0xFF083F22);
        for (int i = 0; i < 9; i++) {
            float x = (0.1f + i * 0.1f) * w + rnd.nextFloat() * 0.03f * w, y = (0.94f + rnd.nextFloat() * 0.04f) * h, r = 0.014f * h;
            fill.setColor(0xFFE0A91B);
            c.drawCircle(x, y, r, fill);
            fill.setColor(0xFFFFD34D);
            c.drawCircle(x - r * 0.15f, y - r * 0.15f, r * 0.7f, fill);
        }
    }

    // ---- shared pieces -----------------------------------------------------------------------------

    private void sky(Canvas c, float w, float h, int top, int mid, int bottom) {
        fill.setShader(new LinearGradient(0, 0, 0, h, new int[] {top, mid, bottom}, new float[] {0f, 0.58f, 1f},
                Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, fill);
        fill.setShader(null);
    }

    private void stars(Canvas c, float w, float h, int count, float maxY, int color) {
        for (int i = 0; i < count; i++) {
            fill.setColor((color & 0xFFFFFF) | ((70 + rnd.nextInt(170)) << 24));
            c.drawCircle(rnd.nextFloat() * w, rnd.nextFloat() * maxY * h, (0.5f + rnd.nextFloat() * 1.3f) * density, fill);
        }
    }

    private void glow(Canvas c, float cx, float cy, float radius, int color) {
        fill.setShader(new RadialGradient(cx, cy, radius, color, color & 0xFFFFFF, Shader.TileMode.CLAMP));
        c.drawCircle(cx, cy, radius, fill);
        fill.setShader(null);
    }

    /** A ridge of low hills filling everything below it. */
    private void hills(Canvas c, float w, float h, float baseY, float amp, int color) {
        float p1 = rnd.nextFloat() * 6f, p2 = rnd.nextFloat() * 6f;
        path.reset();
        path.moveTo(0, h);
        int steps = 60;
        for (int i = 0; i <= steps; i++) {
            float x = w * i / steps;
            float y = baseY - amp * (0.6f * (float) Math.sin(x / w * 5.2f + p1) + 0.4f * (float) Math.sin(x / w * 11f + p2));
            path.lineTo(x, y);
        }
        path.lineTo(w, h);
        path.close();
        fill.setColor(color);
        c.drawPath(path, fill);
    }

    private void cloud(Canvas c, float x, float y, float width, float height) {
        oval.set(x, y, x + width, y + height);
        c.drawOval(oval, fill);
        oval.set(x + width * 0.18f, y - height * 0.55f, x + width * 0.62f, y + height * 0.7f);
        c.drawOval(oval, fill);
        oval.set(x + width * 0.45f, y - height * 0.3f, x + width * 0.92f, y + height * 0.8f);
        c.drawOval(oval, fill);
    }

    private void triangle(Canvas c, float x1, float y1, float x2, float y2, float x3, float y3) {
        path.reset();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        c.drawPath(path, fill);
    }

    /** A bare tree: a trunk that forks into thinner branches. */
    private void tree(Canvas c, float x, float y, float len, float angle, int depth, int seed) {
        if (depth == 0 || len < 2 * density) return;
        double rad = Math.toRadians(angle);
        float x2 = x + (float) Math.cos(rad) * len, y2 = y + (float) Math.sin(rad) * len;
        stroke.setStrokeWidth(Math.max(1f, depth * 0.9f * density));
        c.drawLine(x, y, x2, y2, stroke);
        float spread = 24f + rnd.nextFloat() * 14f;
        tree(c, x2, y2, len * (0.72f + rnd.nextFloat() * 0.1f), angle - spread, depth - 1, seed);
        tree(c, x2, y2, len * (0.72f + rnd.nextFloat() * 0.1f), angle + spread, depth - 1, seed);
        if (depth > 4 && rnd.nextFloat() < 0.5f) tree(c, x2, y2, len * 0.55f, angle + (rnd.nextFloat() - 0.5f) * 20f, depth - 2, seed);
    }

    private void pumpkin(Canvas c, float x, float y, float r) {
        fill.setColor(0xFFD9620F);
        oval.set(x - r * 1.2f, y - r, x + r * 1.2f, y + r);
        c.drawOval(oval, fill);
        fill.setColor(0xFFF28A24);
        oval.set(x - r * 0.55f, y - r, x + r * 0.55f, y + r);
        c.drawOval(oval, fill);
        fill.setColor(0xFF4C7A1E);
        c.drawRect(x - r * 0.1f, y - r * 1.25f, x + r * 0.12f, y - r * 0.85f, fill);
        fill.setColor(0xFF2A1405);
        triangle(c, x - r * 0.55f, y - r * 0.15f, x - r * 0.25f, y - r * 0.5f, x - r * 0.05f, y - r * 0.1f);
        triangle(c, x + r * 0.55f, y - r * 0.15f, x + r * 0.25f, y - r * 0.5f, x + r * 0.05f, y - r * 0.1f);
        path.reset();
        path.moveTo(x - r * 0.6f, y + r * 0.2f);
        path.quadTo(x, y + r * 0.75f, x + r * 0.6f, y + r * 0.2f);
        path.quadTo(x, y + r * 0.4f, x - r * 0.6f, y + r * 0.2f);
        c.drawPath(path, fill);
    }

    /** A snow-capped pine of three overlapping tiers on a short trunk. */
    private void pine(Canvas c, float x, float baseY, float height) {
        fill.setColor(0xFF4A2E1A);
        c.drawRect(x - height * 0.03f, baseY - height * 0.12f, x + height * 0.03f, baseY, fill);
        for (int i = 0; i < 3; i++) {
            float top = baseY - height + i * height * 0.26f;
            float bottom = top + height * 0.42f;
            float half = height * (0.16f + i * 0.09f);
            fill.setColor(i % 2 == 0 ? 0xFF0F4D37 : 0xFF0B3C2A);
            triangle(c, x - half, bottom, x, top, x + half, bottom);
            fill.setColor(0xCCFFFFFF);
            float cap = top + (bottom - top) * 0.5f;
            triangle(c, x - half * 0.5f, cap, x, top, x + half * 0.5f, cap);
        }
    }

    private void snow(Canvas c, float w, float h, int count) {
        for (int i = 0; i < count; i++) {
            fill.setColor(((120 + rnd.nextInt(120)) << 24) | 0xFFFFFF);
            c.drawCircle(rnd.nextFloat() * w, rnd.nextFloat() * h, (0.8f + rnd.nextFloat() * 2.2f) * density, fill);
        }
    }

    private void fourPointStar(Canvas c, float cx, float cy, float r) {
        path.reset();
        path.moveTo(cx, cy - r);
        path.quadTo(cx + r * 0.12f, cy - r * 0.12f, cx + r, cy);
        path.quadTo(cx + r * 0.12f, cy + r * 0.12f, cx, cy + r);
        path.quadTo(cx - r * 0.12f, cy + r * 0.12f, cx - r, cy);
        path.quadTo(cx - r * 0.12f, cy - r * 0.12f, cx, cy - r);
        path.close();
        c.drawPath(path, fill);
    }

    private void firework(Canvas c, float cx, float cy, float r, int color) {
        glow(c, cx, cy, r * 1.6f, (color & 0xFFFFFF) | 0x44000000);
        stroke.setStrokeWidth(Math.max(1.5f * density, r * 0.025f));
        int rays = 22;
        for (int i = 0; i < rays; i++) {
            double a = i * 2 * Math.PI / rays;
            float ca = (float) Math.cos(a), sa = (float) Math.sin(a);
            float len = r * (0.75f + 0.25f * (i % 2));
            stroke.setColor((color & 0xFFFFFF) | 0xE0000000);
            c.drawLine(cx + ca * r * 0.28f, cy + sa * r * 0.28f, cx + ca * len, cy + sa * len, stroke);
            fill.setColor((color & 0xFFFFFF) | 0xFF000000);
            c.drawCircle(cx + ca * len, cy + sa * len, r * 0.035f, fill);
        }
        fill.setColor(0xFFFFFFFF);
        c.drawCircle(cx, cy, r * 0.05f, fill);
    }

    /** An egg with a stripe, a zigzag or dots in a second colour. */
    private void egg(Canvas c, float cx, float cy, float rw, float rh, int base, int accent, int pattern) {
        path.reset();
        path.moveTo(cx, cy - rh);
        path.cubicTo(cx + rw * 1.05f, cy - rh * 0.85f, cx + rw * 1.1f, cy + rh * 0.9f, cx, cy + rh);
        path.cubicTo(cx - rw * 1.1f, cy + rh * 0.9f, cx - rw * 1.05f, cy - rh * 0.85f, cx, cy - rh);
        path.close();
        fill.setColor(base);
        c.drawPath(path, fill);
        c.save();
        c.clipPath(path);
        fill.setColor(accent);
        if (pattern == 0) {
            c.drawRect(cx - rw * 1.2f, cy - rh * 0.12f, cx + rw * 1.2f, cy + rh * 0.12f, fill);
            c.drawRect(cx - rw * 1.2f, cy + rh * 0.38f, cx + rw * 1.2f, cy + rh * 0.5f, fill);
        } else if (pattern == 1) {
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    if ((i + j) % 2 == 0) c.drawCircle(cx + i * rw * 0.42f, cy + j * rh * 0.36f, rw * 0.11f, fill);
                }
            }
        } else {
            stroke.setColor(accent);
            stroke.setStrokeWidth(rw * 0.12f);
            path.reset();
            float y = cy - rh * 0.1f;
            path.moveTo(cx - rw * 1.2f, y);
            for (int i = 0; i < 8; i++) path.lineTo(cx - rw * 1.2f + (i + 1) * rw * 0.3f, y + (i % 2 == 0 ? -1 : 1) * rh * 0.12f);
            c.drawPath(path, stroke);
        }
        c.restore();
    }

    private void heart(Canvas c, float cx, float cy, float size, float rotation) {
        c.save();
        c.translate(cx, cy);
        c.rotate(rotation);
        c.scale(size, size);
        path.reset();
        path.moveTo(0f, 0.42f);
        path.cubicTo(-0.62f, 0.02f, -0.52f, -0.46f, 0f, -0.16f);
        path.cubicTo(0.52f, -0.46f, 0.62f, 0.02f, 0f, 0.42f);
        path.close();
        c.drawPath(path, fill);
        c.restore();
    }

    private void clover(Canvas c, float cx, float cy, float size, float rotation) {
        c.save();
        c.translate(cx, cy);
        c.rotate(rotation);
        c.scale(size, size);
        c.drawCircle(-0.2f, -0.2f, 0.21f, fill);
        c.drawCircle(0.2f, -0.2f, 0.21f, fill);
        c.drawCircle(-0.2f, 0.2f, 0.21f, fill);
        c.drawCircle(0.2f, 0.2f, 0.21f, fill);
        c.drawRect(-0.04f, 0.1f, 0.04f, 0.58f, fill);
        c.restore();
    }

    /** Darkens the lower half and the top left so the dock, clock and greeting read over any scene. */
    private void shade(Canvas c, float w, float h) {
        fill.setShader(new LinearGradient(0, h * 0.5f, 0, h, 0x00000000, 0xB8000000, Shader.TileMode.CLAMP));
        c.drawRect(0, h * 0.5f, w, h, fill);
        fill.setShader(new RadialGradient(0, 0, w * 0.55f, 0x80000000, 0x00000000, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w * 0.6f, h * 0.6f, fill);
        fill.setShader(null);
    }
}
