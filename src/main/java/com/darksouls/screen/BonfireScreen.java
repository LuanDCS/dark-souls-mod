package com.darksouls.screen;

import com.darksouls.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

/**
 * Dark Souls bonfire rest menu. Darkens the screen and displays a vertical
 * list of options. Mouse hovers play a cue sound; only "Sair" is wired up.
 */
public final class BonfireScreen extends Screen {
    private static final int COLOR_OVERLAY    = 0xCC000000;
    private static final int COLOR_TEXT       = 0xFFE8D8B0;
    private static final int COLOR_TEXT_HOVER = 0xFFFFE0A8;
    private static final int COLOR_TEXT_DIM   = 0xFF555044;
    private static final int COLOR_TITLE      = 0xFFC87B3A;

    private static final String[] OPTIONS = {
            "Sair",
            "Aumentar nível",
            "Atribuir magia",
            "Acessar baú",
            "Teleportar",
            "Acender bonfire",
            "Reverter estado vazio"
    };
    private static final int TELEPORT_INDEX = 4;

    private static final int ROW_HEIGHT = 22;
    private static final int ROW_WIDTH  = 260;

    private int hoveredRow = -1;
    private int listX;
    private int listY;

    public BonfireScreen() {
        super(Text.literal("Fogueira"));
    }

    @Override
    protected void init() {
        listX = (this.width - ROW_WIDTH) / 2;
        listY = this.height / 2 - (OPTIONS.length * ROW_HEIGHT) / 2;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, COLOR_OVERLAY);
        super.render(ctx, mouseX, mouseY, delta);

        int titleW = this.textRenderer.getWidth("Fogueira");
        ctx.drawText(this.textRenderer, Text.literal("Fogueira"),
                (this.width - titleW) / 2, listY - 30, COLOR_TITLE, true);

        int currentHover = -1;
        for (int i = 0; i < OPTIONS.length; i++) {
            int rowY = listY + i * ROW_HEIGHT;
            boolean disabled = i == TELEPORT_INDEX;
            boolean hover = !disabled
                    && mouseX >= listX && mouseX < listX + ROW_WIDTH
                    && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
            if (hover) currentHover = i;

            int color = disabled ? COLOR_TEXT_DIM : (hover ? COLOR_TEXT_HOVER : COLOR_TEXT);
            String label = OPTIONS[i];
            int labelW = this.textRenderer.getWidth(label);
            int textX = listX + (ROW_WIDTH - labelW) / 2;
            int textY = rowY + (ROW_HEIGHT - this.textRenderer.fontHeight) / 2;

            if (hover) {
                ctx.fill(listX, rowY, listX + ROW_WIDTH, rowY + ROW_HEIGHT, 0x33C87B3A);
            }
            ctx.drawText(this.textRenderer, Text.literal(label), textX, textY, color, true);
        }

        if (currentHover >= 0 && currentHover != hoveredRow) {
            playUiSound(ModSounds.UI_MOUSEMENU);
        }
        hoveredRow = currentHover;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < OPTIONS.length; i++) {
                if (i == TELEPORT_INDEX) continue;
                int rowY = listY + i * ROW_HEIGHT;
                if (mouseX >= listX && mouseX < listX + ROW_WIDTH
                        && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT) {
                    onOptionClicked(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void onOptionClicked(int index) {
        if (index == 0) {
            this.close();
        }
    }

    @Override
    public void close() {
        playUiSound(ModSounds.UI_MENU_BACK);
        super.close();
    }

    private static void playUiSound(SoundEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        client.getSoundManager().play(PositionedSoundInstance.master(event, 1.0f));
    }
}
