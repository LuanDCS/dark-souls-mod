package com.darksouls.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Short cinematic overlay shown when a bonfire is lit for the first time.
 */
public final class BonfireLitScreen extends Screen {
    private static final int DURATION_TICKS = 58;
    private static final float TEXT_SCALE = 2.2f;
    private static final int COLOR_TEXT_MAIN = 0xFFD58B3B;
    private static final int COLOR_TEXT_GLOW = 0xAA7A3E16;

    private int ticksLeft = DURATION_TICKS;

    public BonfireLitScreen() {
        super(Text.literal("BONFIRE LIT"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        ticksLeft--;
        if (ticksLeft <= 0) {
            this.close();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        float elapsed = DURATION_TICKS - ticksLeft + delta;
        float phaseIn = clamp01(elapsed / 9.0f);
        float phaseOut = clamp01(ticksLeft / 16.0f);
        float alphaFactor = Math.min(phaseIn, phaseOut);

        int screenDark = (int) (90 * alphaFactor);
        int vignetteAlpha = (int) (200 * alphaFactor);

        ctx.fill(0, 0, this.width, this.height, (screenDark << 24) | 0x000000);

        drawVignette(ctx, vignetteAlpha);

        String label = "BONFIRE LIT";
        float pulse = 0.97f + 0.03f * (float) Math.sin(elapsed * 0.25f);
        float scale = TEXT_SCALE * pulse;
        float sw = this.textRenderer.getWidth(label) * scale;
        float sh = this.textRenderer.fontHeight * scale;
        float x = (this.width - sw) * 0.5f;
        float y = (this.height - sh) * 0.49f;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0.0f);
        ctx.getMatrices().scale(scale, scale, 1.0f);

        Text text = Text.literal(label);
        ctx.drawText(this.textRenderer, text, -1, 0, COLOR_TEXT_GLOW, false);
        ctx.drawText(this.textRenderer, text, 1, 0, COLOR_TEXT_GLOW, false);
        ctx.drawText(this.textRenderer, text, 0, -1, COLOR_TEXT_GLOW, false);
        ctx.drawText(this.textRenderer, text, 0, 1, COLOR_TEXT_GLOW, false);
        ctx.drawText(this.textRenderer, text, 0, 0, COLOR_TEXT_MAIN, false);
        ctx.getMatrices().pop();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return true;
    }

    private void drawVignette(DrawContext ctx, int alpha) {
        int layers = 5;
        int maxEdge = Math.max(24, Math.min(this.width, this.height) / 6);
        for (int i = 0; i < layers; i++) {
            float t = (i + 1) / (float) layers;
            int edge = (int) (maxEdge * t);
            int a = (int) (alpha * (1.0f - (i / (float) (layers + 1))));
            int color = (a << 24) | 0x000000;
            ctx.fill(0, 0, this.width, edge, color);
            ctx.fill(0, this.height - edge, this.width, this.height, color);
            ctx.fill(0, 0, edge, this.height, color);
            ctx.fill(this.width - edge, 0, this.width, this.height, color);
        }
    }

    private static float clamp01(float v) {
        if (v < 0.0f) return 0.0f;
        if (v > 1.0f) return 1.0f;
        return v;
    }
}
