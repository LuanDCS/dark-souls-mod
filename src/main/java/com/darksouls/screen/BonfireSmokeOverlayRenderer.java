package com.darksouls.screen;

import com.darksouls.DarkSoulsMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Fullscreen bonfire smoke overlay with 3 layers (base -> layer1 -> layer2),
 * UV scrolling and fade-in/fade-out timings.
 */
public final class BonfireSmokeOverlayRenderer {
    private static final Identifier TEX_BASE = DarkSoulsMod.id("textures/gui/effects/layerbase.png");
    private static final Identifier TEX_LAYER1 = DarkSoulsMod.id("textures/gui/effects/layer1.png");
    private static final Identifier TEX_LAYER2 = DarkSoulsMod.id("textures/gui/effects/layer2.png");

    private static final int BASE_W = 1024;
    private static final int BASE_H = 1024;
    private static final int LAYER1_W = 1024;
    private static final int LAYER1_H = 1024;
    private static final int LAYER2_W = 1408;
    private static final int LAYER2_H = 768;

    private static final float BASE_ALPHA = 0.45f;
    private static final float LAYER1_ALPHA = 0.25f;
    private static final float LAYER2_ALPHA = 0.15f;

    // Requested UV speeds.
    private static final float LAYER1_SPEED_X = 0.002f;
    private static final float LAYER1_SPEED_Y = 0.001f;
    private static final float LAYER2_SPEED_X = -0.0015f;
    private static final float LAYER2_SPEED_Y = 0.0025f;

    // Base almost static.
    private static final float BASE_SPEED_X = 0.00025f;
    private static final float BASE_SPEED_Y = 0.00010f;

    private static final float FADE_IN_SECONDS = 0.400f;
    private static final float FADE_OUT_SECONDS = 0.300f;

    private static boolean overlayActive = false;
    private static float fade = 0.0f;
    private static float baseU = 0.0f;
    private static float baseV = 0.0f;
    private static float layer1U = 0.0f;
    private static float layer1V = 0.0f;
    private static float layer2U = 0.0f;
    private static float layer2V = 0.0f;
    private static long lastFrameNanos = -1L;

    private BonfireSmokeOverlayRenderer() {}

    public static void startOverlay() {
        overlayActive = true;
    }

    public static void stopOverlay() {
        overlayActive = false;
    }

    public static void reset() {
        overlayActive = false;
        fade = 0.0f;
        baseU = 0.0f;
        baseV = 0.0f;
        layer1U = 0.0f;
        layer1V = 0.0f;
        layer2U = 0.0f;
        layer2V = 0.0f;
        lastFrameNanos = -1L;
    }

    public static boolean isVisible() {
        return fade > 0.001f;
    }

    public static void render(DrawContext ctx, int width, int height, boolean withVignette) {
        updateState();
        if (fade <= 0.001f) {
            return;
        }

        int backDarkAlpha = (int) (28 * fade);
        ctx.fill(0, 0, width, height, (backDarkAlpha << 24) | 0x000000);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        drawLayer(ctx, TEX_BASE, BASE_W, BASE_H, BASE_ALPHA * fade, baseU, baseV, width, height);
        drawLayer(ctx, TEX_LAYER1, LAYER1_W, LAYER1_H, LAYER1_ALPHA * fade, layer1U, layer1V, width, height);
        drawLayer(ctx, TEX_LAYER2, LAYER2_W, LAYER2_H, LAYER2_ALPHA * fade, layer2U, layer2V, width, height);

        if (withVignette) {
            drawVignette(ctx, width, height, fade);
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void updateState() {
        long now = System.nanoTime();
        if (lastFrameNanos < 0L) {
            lastFrameNanos = now;
            return;
        }

        float deltaSeconds = (now - lastFrameNanos) / 1_000_000_000.0f;
        lastFrameNanos = now;
        if (deltaSeconds < 0.0f) deltaSeconds = 0.0f;
        if (deltaSeconds > 0.100f) deltaSeconds = 0.100f;

        float frameScale = deltaSeconds * 60.0f;
        baseU += BASE_SPEED_X * frameScale;
        baseV += BASE_SPEED_Y * frameScale;
        layer1U += LAYER1_SPEED_X * frameScale;
        layer1V += LAYER1_SPEED_Y * frameScale;
        layer2U += LAYER2_SPEED_X * frameScale;
        layer2V += LAYER2_SPEED_Y * frameScale;

        if (overlayActive) {
            fade += deltaSeconds / FADE_IN_SECONDS;
        } else {
            fade -= deltaSeconds / FADE_OUT_SECONDS;
        }
        if (fade < 0.0f) fade = 0.0f;
        if (fade > 1.0f) fade = 1.0f;
    }

    private static void drawLayer(DrawContext ctx, Identifier texture, int texW, int texH,
                                  float alpha, float uOffset, float vOffset,
                                  int screenW, int screenH) {
        if (alpha <= 0.001f) return;

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        int scrollX = wrapToTile((int) (uOffset * texW), texW);
        int scrollY = wrapToTile((int) (vOffset * texH), texH);
        int startX = -scrollX - texW;
        int startY = -scrollY - texH;

        for (int y = startY; y < screenH + texH; y += texH) {
            for (int x = startX; x < screenW + texW; x += texW) {
                ctx.drawTexture(texture, x, y, texW, texH, 0.0f, 0.0f, texW, texH, texW, texH);
            }
        }
    }

    private static void drawVignette(DrawContext ctx, int width, int height, float fadeValue) {
        int layers = 4;
        int maxEdge = Math.max(24, Math.min(width, height) / 7);
        for (int i = 0; i < layers; i++) {
            float t = (i + 1) / (float) layers;
            int edge = (int) (maxEdge * t);
            int alpha = (int) (95 * fadeValue * (1.0f - (i / (float) (layers + 1))));
            int color = (alpha << 24) | 0x000000;
            ctx.fill(0, 0, width, edge, color);
            ctx.fill(0, height - edge, width, height, color);
            ctx.fill(0, 0, edge, height, color);
            ctx.fill(width - edge, 0, width, height, color);
        }
    }

    private static int wrapToTile(int value, int tile) {
        int m = value % tile;
        if (m < 0) m += tile;
        return m;
    }
}
