package com.darksouls.hud;

import com.darksouls.stats.ClientStats;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Dark Souls 1 inspired HUD in the upper-left corner.
 *
 * Layout (left to right):
 *   [ humanity circle ]  [ HP bar          ]
 *                        [ stamina bar  ]  (narrower than HP, DS1-style)
 *
 * All dimensions come from {@link HudConfig} so the future stats system can
 * resize the bars by mutating that class without touching the renderer.
 */
public final class DarkSoulsHud implements HudRenderCallback {
    private static final int COLOR_BORDER      = 0xFF000000;
    private static final int COLOR_FRAME       = 0xFF2A2A2A;
    private static final int COLOR_BG          = 0xFF1A0A08;
    private static final int COLOR_HP_FILL     = 0xFFB23030;
    private static final int COLOR_HP_HILITE   = 0xFFE25858;
    private static final int COLOR_STAM_FILL   = 0xFF3E8A3E;
    private static final int COLOR_STAM_HILITE = 0xFF66B866;
    private static final int COLOR_CIRCLE_BG   = 0xFF0F0F0F;
    private static final int COLOR_CIRCLE_RING = 0xFF8A8A8A;
    private static final int COLOR_CIRCLE_HI   = 0xFFC8C8C8;

    private float animatedHp = -1f;
    private float animatedStam = -1f;

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.player == null || mc.player.isSpectator()) return;

        ClientPlayerEntity p = mc.player;
        float hpFraction = clamp01(p.getHealth() / Math.max(1f, p.getMaxHealth()));
        float stamFraction = ClientStats.staminaFraction();

        if (animatedHp < 0) animatedHp = hpFraction;
        if (animatedStam < 0) animatedStam = stamFraction;
        animatedHp = approach(animatedHp, hpFraction, 0.20f);
        animatedStam = approach(animatedStam, stamFraction, 0.35f);

        int circleX = HudConfig.marginX;
        int circleY = HudConfig.marginY;
        int barsX = circleX + HudConfig.circleSize + HudConfig.circleGap;
        int hpY = circleY + (HudConfig.circleSize - (HudConfig.hpBarHeight + HudConfig.barGap + HudConfig.staminaBarHeight)) / 2;
        int stamY = hpY + HudConfig.hpBarHeight + HudConfig.barGap;

        drawBar(ctx, barsX, hpY, HudConfig.hpBarWidth(), HudConfig.hpBarHeight,
                animatedHp, COLOR_HP_FILL, COLOR_HP_HILITE);
        drawBar(ctx, barsX, stamY, HudConfig.staminaBarWidth(), HudConfig.staminaBarHeight,
                animatedStam, COLOR_STAM_FILL, COLOR_STAM_HILITE);

        drawHumanityCircle(ctx, circleX, circleY, HudConfig.circleSize, ClientStats.humanity());
    }

    private static void drawBar(DrawContext ctx, int x, int y, int w, int h,
                                float fraction, int fill, int hilite) {
        ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, COLOR_BORDER);
        ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, COLOR_FRAME);
        ctx.fill(x, y, x + w, y + h, COLOR_BG);
        int fillW = Math.round(w * fraction);
        if (fillW > 0) {
            ctx.fill(x, y, x + fillW, y + h, fill);
            ctx.fill(x, y, x + fillW, y + 1, hilite);
        }
    }

    /**
     * Circular slot for humanity. Software-drawn on the pixel grid (no texture needed):
     *   - dark inner disc
     *   - light inner ring (highlight)
     *   - darker outer rim
     * Number rendered centered with a drop shadow.
     */
    private static void drawHumanityCircle(DrawContext ctx, int x, int y, int size, int humanity) {
        float r = size / 2.0f;
        float cx = x + r;
        float cy = y + r;
        float rOuter = r;
        float rRing = r - 1.25f;
        float rInner = r - 2.75f;

        for (int py = 0; py < size; py++) {
            for (int px = 0; px < size; px++) {
                float ddx = (x + px + 0.5f) - cx;
                float ddy = (y + py + 0.5f) - cy;
                float d2 = ddx * ddx + ddy * ddy;
                int color;
                if (d2 <= rInner * rInner) color = COLOR_CIRCLE_BG;
                else if (d2 <= rRing * rRing) color = COLOR_CIRCLE_HI;
                else if (d2 <= rOuter * rOuter) color = COLOR_CIRCLE_RING;
                else continue;
                ctx.fill(x + px, y + py, x + px + 1, y + py + 1, color);
            }
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        String label = Integer.toString(humanity);
        int tw = mc.textRenderer.getWidth(label);
        int tx = Math.round(cx - tw / 2.0f);
        int ty = Math.round(cy - mc.textRenderer.fontHeight / 2.0f) + 1;
        ctx.drawText(mc.textRenderer, label, tx, ty, 0xFFFFFFFF, true);
    }

    private static float approach(float current, float target, float speed) {
        float diff = target - current;
        if (Math.abs(diff) < 0.001f) return target;
        return current + diff * speed;
    }

    private static float clamp01(float v) {
        return v < 0f ? 0f : (v > 1f ? 1f : v);
    }
}
