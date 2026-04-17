package com.darksouls.screen;

import com.darksouls.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
<<<<<<< HEAD

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
=======
import org.lwjgl.glfw.GLFW;

/**
 * Dark Souls 1 inspired bonfire menu.
 *
 * Visual goals:
 * - Anchored at the left side of the screen.
 * - Dark framed panel with subtle horizontal separators.
 * - Orange highlight bar for the focused option.
 */
public final class BonfireScreen extends Screen {
    private static final int COLOR_SCREEN_OVERLAY = 0xA0000000;
    private static final int COLOR_PANEL_BORDER_1 = 0xFF030303;
    private static final int COLOR_PANEL_BORDER_2 = 0xFF141414;
    private static final int COLOR_PANEL_BG = 0xD0080808;
    private static final int COLOR_INNER_FRAME = 0xAA1C1C1C;
    private static final int COLOR_ROW_LINE = 0x553A3328;
    private static final int COLOR_ROW_SELECTED = 0xAA8E2D10;
    private static final int COLOR_TEXT = 0xFFE9E2D2;
    private static final int COLOR_TEXT_SELECTED = 0xFFFFF2D8;
    private static final int COLOR_TEXT_HINT = 0xFFB8B0A2;

    private static final String[] OPTIONS = {
            "Leave",
            "Level Up",
            "Covenant",
            "Kindle",
            "Reverse Hollowing"
    };

    private static final int PANEL_W = 236;
    private static final int PANEL_H = 276;
    private static final int ROW_HEIGHT = 20;
    private static final int LIST_TOP_PAD = 36;
    private static final int LIST_SIDE_PAD = 14;
    private static final int FOOTER_PAD = 14;

    private int selectedRow = 0;
    private int panelX;
    private int panelY;
    private int listX;
    private int listY;
    private int listW;
    private int listH;
    private boolean closingWithFade;

    public BonfireScreen() {
        super(Text.literal("Bonfire"));
>>>>>>> ba47194 (Melhora no sistema de BonFire)
    }

    @Override
    protected void init() {
<<<<<<< HEAD
        listX = (this.width - ROW_WIDTH) / 2;
        listY = this.height / 2 - (OPTIONS.length * ROW_HEIGHT) / 2;
=======
        panelX = 14;
        panelY = Math.max(12, (this.height - PANEL_H) / 2);

        listX = panelX + LIST_SIDE_PAD;
        listY = panelY + LIST_TOP_PAD;
        listW = PANEL_W - (LIST_SIDE_PAD * 2);
        listH = PANEL_H - LIST_TOP_PAD - FOOTER_PAD;

        // Menu opened after the rest transition: smoke should keep visible for a
        // brief moment and then fade out while the menu is already on screen.
        BonfireSmokeOverlayRenderer.stopOverlay();

        playUiSound(ModSounds.UI_MENU_OPEN);
>>>>>>> ba47194 (Melhora no sistema de BonFire)
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
<<<<<<< HEAD
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
=======
    public void tick() {
        if (closingWithFade && !BonfireSmokeOverlayRenderer.isVisible()) {
            super.close();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, COLOR_SCREEN_OVERLAY);
        super.render(ctx, mouseX, mouseY, delta);

        BonfireSmokeOverlayRenderer.render(ctx, this.width, this.height, true);

        drawPanel(ctx);

        int hoverRow = rowAt(mouseX, mouseY);
        if (hoverRow >= 0 && hoverRow != selectedRow) {
            selectedRow = hoverRow;
            playUiSound(ModSounds.UI_MOUSEMENU);
        }

        int visibleRows = Math.min(OPTIONS.length, listH / ROW_HEIGHT);
        int rowsBottomY = listY + visibleRows * ROW_HEIGHT;
        for (int y = listY; y <= rowsBottomY; y += ROW_HEIGHT) {
            ctx.fill(listX, y, listX + listW, y + 1, COLOR_ROW_LINE);
        }

        for (int i = 0; i < OPTIONS.length; i++) {
            int rowY = listY + i * ROW_HEIGHT;
            boolean focused = i == selectedRow;
            if (focused) {
                ctx.fill(listX, rowY + 1, listX + listW, rowY + ROW_HEIGHT - 1, COLOR_ROW_SELECTED);
            }

            int textY = rowY + (ROW_HEIGHT - this.textRenderer.fontHeight) / 2 - 1;
            int color = focused ? COLOR_TEXT_SELECTED : COLOR_TEXT;
            ctx.drawText(this.textRenderer, Text.literal(OPTIONS[i]), listX + 8, textY, color, true);
        }

>>>>>>> ba47194 (Melhora no sistema de BonFire)
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
<<<<<<< HEAD
        if (button == 0) {
            for (int i = 0; i < OPTIONS.length; i++) {
                if (i == TELEPORT_INDEX) continue;
                int rowY = listY + i * ROW_HEIGHT;
                if (mouseX >= listX && mouseX < listX + ROW_WIDTH
                        && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT) {
                    onOptionClicked(i);
                    return true;
                }
=======
        if (closingWithFade) return true;
        if (button == 0) {
            int row = rowAt(mouseX, mouseY);
            if (row >= 0) {
                selectedRow = row;
                playUiSound(ModSounds.UI_MENU_OPEN);
                onOptionClicked(selectedRow);
                return true;
>>>>>>> ba47194 (Melhora no sistema de BonFire)
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

<<<<<<< HEAD
    private void onOptionClicked(int index) {
        if (index == 0) {
            this.close();
=======
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (closingWithFade) return true;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            selectedRow = (selectedRow - 1 + OPTIONS.length) % OPTIONS.length;
            playUiSound(ModSounds.UI_MOUSEMENU);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            selectedRow = (selectedRow + 1) % OPTIONS.length;
            playUiSound(ModSounds.UI_MOUSEMENU);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
            playUiSound(ModSounds.UI_MENU_OPEN);
            onOptionClicked(selectedRow);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onOptionClicked(int index) {
        if (index == 0) {
            this.close();
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.literal(OPTIONS[index] + " (placeholder)"), true);
>>>>>>> ba47194 (Melhora no sistema de BonFire)
        }
    }

    @Override
    public void close() {
<<<<<<< HEAD
        playUiSound(ModSounds.UI_MENU_BACK);
=======
        if (!closingWithFade) {
            closingWithFade = true;
            BonfireSmokeOverlayRenderer.stopOverlay();
            playUiSound(ModSounds.UI_MENU_BACK);
            return;
        }
>>>>>>> ba47194 (Melhora no sistema de BonFire)
        super.close();
    }

    private static void playUiSound(SoundEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        client.getSoundManager().play(PositionedSoundInstance.master(event, 1.0f));
    }
<<<<<<< HEAD
=======

    private int rowAt(double mouseX, double mouseY) {
        if (mouseX < listX || mouseX >= listX + listW) return -1;
        if (mouseY < listY || mouseY >= listY + OPTIONS.length * ROW_HEIGHT) return -1;
        int row = (int) ((mouseY - listY) / ROW_HEIGHT);
        return (row >= 0 && row < OPTIONS.length) ? row : -1;
    }

    private void drawPanel(DrawContext ctx) {
        ctx.fill(panelX - 2, panelY - 2, panelX + PANEL_W + 2, panelY + PANEL_H + 2, COLOR_PANEL_BORDER_1);
        ctx.fill(panelX - 1, panelY - 1, panelX + PANEL_W + 1, panelY + PANEL_H + 1, COLOR_PANEL_BORDER_2);
        ctx.fill(panelX, panelY, panelX + PANEL_W, panelY + PANEL_H, COLOR_PANEL_BG);

        ctx.fill(panelX + 10, panelY + 24, panelX + PANEL_W - 10, panelY + 25, COLOR_INNER_FRAME);
        ctx.fill(panelX + 10, panelY + 25, panelX + 11, panelY + PANEL_H - 26, COLOR_INNER_FRAME);
        ctx.fill(panelX + PANEL_W - 11, panelY + 25, panelX + PANEL_W - 10, panelY + PANEL_H - 26, COLOR_INNER_FRAME);
        ctx.fill(panelX + 10, panelY + PANEL_H - 26, panelX + PANEL_W - 10, panelY + PANEL_H - 25, COLOR_INNER_FRAME);
    }
>>>>>>> ba47194 (Melhora no sistema de BonFire)
}
