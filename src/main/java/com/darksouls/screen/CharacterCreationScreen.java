package com.darksouls.screen;

import com.darksouls.DarkSoulsMod;
import com.darksouls.network.ModNetworking;
import com.darksouls.sound.ModSounds;
import com.darksouls.stats.CharacterClass;
import com.darksouls.stats.ClientStats;
import com.darksouls.stats.Gift;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * DS1-inspired character creation screen.
 *
 * Right-side panel swaps between the class list and the gift list depending on
 * whether the user clicked "classe:" or "dádiva:" in the top-left block.
 * Confirm/Cancel are custom-drawn coloured buttons pinned to the bottom-right.
 *
 * All stat lines are driven live by {@code selectedClass}; toggling a class in
 * the list re-reads the stats on the next frame.
 */
public final class CharacterCreationScreen extends Screen {
    private enum RightPanel { CLASSES, GIFTS }

    private static final int COLOR_SCREEN_BG   = 0xEE0A0705;
    private static final int COLOR_PANEL_BG    = 0xFF1A0A08;
    private static final int COLOR_PANEL_FRAME = 0xFF2A2A2A;
    private static final int COLOR_BORDER      = 0xFF000000;
    private static final int COLOR_TEXT        = 0xFFE8D8B0;
    private static final int COLOR_TEXT_DIM    = 0xFF887760;
    private static final int COLOR_TEXT_ACCENT = 0xFFC87B3A;
    private static final int COLOR_TEXT_SEL    = 0xFFFFE0A8;
    private static final int COLOR_FIELD_BG    = 0xFF0E0604;
    private static final int COLOR_FIELD_HOT   = 0xFF1E1008;
    private static final int COLOR_ROW_HOVER   = 0xFF2A1410;
    private static final int COLOR_ROW_SEL     = 0xFF4A1E10;

    private static final int COLOR_BTN_GREEN       = 0xFF2E7E2E;
    private static final int COLOR_BTN_GREEN_HOVER = 0xFF3E9E3E;
    private static final int COLOR_BTN_RED         = 0xFF9E2A2A;
    private static final int COLOR_BTN_RED_HOVER   = 0xFFBE3A3A;
    private static final int COLOR_BTN_FRAME       = 0xFF000000;

    private static final int FIELD_WIDTH   = 150;
    private static final int FIELD_HEIGHT  = 16;
    private static final int STAT_ROW_H    = 17;
    private static final int ICON_SIZE     = 14;
    private static final int LIST_ROW_H    = 18;
    private static final int BTN_W         = 120;
    private static final int BTN_H         = 26;

    private static final Identifier ICON_LEVEL        = DarkSoulsMod.id("textures/gui/stats/icon_level.png");
    private static final Identifier ICON_VITALITY     = DarkSoulsMod.id("textures/gui/stats/icon_vitality.png");
    private static final Identifier ICON_ATTUNEMENT   = DarkSoulsMod.id("textures/gui/stats/icon_attunement.png");
    private static final Identifier ICON_ENDURANCE    = DarkSoulsMod.id("textures/gui/stats/icon_endurance.png");
    private static final Identifier ICON_STRENGTH     = DarkSoulsMod.id("textures/gui/stats/icon_strength.png");
    private static final Identifier ICON_DEXTERITY    = DarkSoulsMod.id("textures/gui/stats/icon_dexterity.png");
    private static final Identifier ICON_RESISTANCE   = DarkSoulsMod.id("textures/gui/stats/icon_resistance.png");
    private static final Identifier ICON_INTELLIGENCE = DarkSoulsMod.id("textures/gui/stats/icon_intelligence.png");
    private static final Identifier ICON_FAITH        = DarkSoulsMod.id("textures/gui/stats/icon_faith.png");
    private static final Identifier ICON_HUMANITY     = DarkSoulsMod.id("textures/gui/stats/icon_humanity.png");

    private TextFieldWidget nameField;
    private CharacterClass selectedClass = CharacterClass.KNIGHT;
    private Gift selectedGift = Gift.NONE;
    private RightPanel activePanel = RightPanel.CLASSES;

    private int classFieldX, classFieldY;
    private int giftFieldX, giftFieldY;
    private int listX, listY, listW, listH;
    private int confirmX, confirmY, cancelX, cancelY;
    private int hoveredListRow = -1;

    public CharacterCreationScreen() {
        super(Text.literal("Criar Personagem"));
    }

    @Override
    protected void init() {
        int panelX = Math.max(20, (this.width - 780) / 2);
        int panelY = 40;

        nameField = new TextFieldWidget(this.textRenderer, panelX + 74, panelY + 10,
                FIELD_WIDTH, FIELD_HEIGHT, Text.literal("Nome"));
        nameField.setMaxLength(24);
        nameField.setText(ClientStats.character.name.isEmpty() ? "Escolhido" : ClientStats.character.name);
        this.addDrawableChild(nameField);

        classFieldX = panelX + 74;
        classFieldY = panelY + 30;
        giftFieldX = panelX + 74;
        giftFieldY = panelY + 50;

        listX = panelX + 256;
        listY = panelY;
        listW = 190;
        listH = LIST_ROW_H * Math.max(CharacterClass.values().length, Gift.values().length) + 12;

        confirmX = this.width - BTN_W - 20;
        cancelX  = confirmX - BTN_W - 10;
        confirmY = this.height - BTN_H - 16;
        cancelY  = confirmY;

        this.setInitialFocus(nameField);
        playUiSound(ModSounds.UI_MENU_OPEN);
    }

    private void onConfirm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = "Escolhido";
        ModNetworking.sendCreate(name, selectedClass, selectedGift);
        this.close();
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Override
    public void close() {
        playUiSound(ModSounds.UI_MENU_BACK);
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (inRect(mouseX, mouseY, confirmX, confirmY, BTN_W, BTN_H)) { onConfirm(); return true; }
            if (inRect(mouseX, mouseY, cancelX, cancelY, BTN_W, BTN_H))   { this.close();  return true; }
            if (inRect(mouseX, mouseY, classFieldX, classFieldY, FIELD_WIDTH, FIELD_HEIGHT)) {
                activePanel = RightPanel.CLASSES;
                hoveredListRow = -1;
                playUiSound(ModSounds.UI_MENU_OPEN);
                return true;
            }
            if (inRect(mouseX, mouseY, giftFieldX, giftFieldY, FIELD_WIDTH, FIELD_HEIGHT)) {
                activePanel = RightPanel.GIFTS;
                hoveredListRow = -1;
                playUiSound(ModSounds.UI_MENU_OPEN);
                return true;
            }
            if (inRect(mouseX, mouseY, listX, listY, listW, listH)) {
                int row = (int) ((mouseY - (listY + 6)) / LIST_ROW_H);
                if (activePanel == RightPanel.CLASSES) {
                    CharacterClass[] all = CharacterClass.values();
                    if (row >= 0 && row < all.length) {
                        selectedClass = all[row];
                        playUiSound(ModSounds.UI_MENU_OPEN);
                        return true;
                    }
                } else {
                    Gift[] all = Gift.values();
                    if (row >= 0 && row < all.length) {
                        selectedGift = all[row];
                        playUiSound(ModSounds.UI_MENU_OPEN);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, COLOR_SCREEN_BG);
        super.render(ctx, mouseX, mouseY, delta);

        int panelX = Math.max(20, (this.width - 780) / 2);
        int panelY = 40;

        ctx.drawText(this.textRenderer, Text.literal("Criar Personagem"), panelX, panelY - 18, COLOR_TEXT_ACCENT, true);

        drawPanel(ctx, panelX, panelY, 240, 74);
        drawFieldLabel(ctx, panelX + 10, panelY + 14, "nome:");
        drawFieldLabel(ctx, panelX + 10, panelY + 34, "classe:");
        drawFieldLabel(ctx, panelX + 10, panelY + 54, "dádiva:");

        boolean classHot = inRect(mouseX, mouseY, classFieldX, classFieldY, FIELD_WIDTH, FIELD_HEIGHT);
        boolean giftHot  = inRect(mouseX, mouseY, giftFieldX,  giftFieldY,  FIELD_WIDTH, FIELD_HEIGHT);
        drawClickableField(ctx, classFieldX, classFieldY, FIELD_WIDTH, FIELD_HEIGHT,
                selectedClass.displayName, COLOR_TEXT,
                activePanel == RightPanel.CLASSES, classHot);
        String giftLabel = selectedGift == Gift.NONE ? "—" : selectedGift.displayName;
        drawClickableField(ctx, giftFieldX, giftFieldY, FIELD_WIDTH, FIELD_HEIGHT,
                giftLabel, selectedGift == Gift.NONE ? COLOR_TEXT_DIM : COLOR_TEXT,
                activePanel == RightPanel.GIFTS, giftHot);

        int statsY = panelY + 92;
        int statsW = 240;
        int statsH = STAT_ROW_H * 10 + 12;
        drawPanel(ctx, panelX, statsY, statsW, statsH);
        int rx = panelX + 12;
        int row = 0;
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_LEVEL,        "nível",        selectedClass.level);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_VITALITY,     "vitalidade",   selectedClass.vitality);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_ATTUNEMENT,   "conhecimento", selectedClass.attunement);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_ENDURANCE,    "fortitude",    selectedClass.endurance);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_STRENGTH,     "força",        selectedClass.strength);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_DEXTERITY,    "destreza",     selectedClass.dexterity);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_RESISTANCE,   "resistência",  selectedClass.resistance);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_INTELLIGENCE, "inteligência", selectedClass.intelligence);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_FAITH,        "fé",           selectedClass.faith);
        drawStatRow(ctx, rx, statsY + 8 + STAT_ROW_H * row++, ICON_HUMANITY,     "humanidade",   0);

        drawPanel(ctx, listX, listY, listW, listH);
        if (activePanel == RightPanel.CLASSES) {
            renderList(ctx, mouseX, mouseY, CharacterClass.values(), c -> c.displayName, selectedClass.ordinal());
        } else {
            renderList(ctx, mouseX, mouseY, Gift.values(), g -> g.displayName, selectedGift.ordinal());
        }

        int previewX = listX + listW + 20;
        int previewY = panelY;
        int previewW = Math.max(140, this.width - previewX - 32);
        int previewH = this.height - previewY - 72;
        drawPanel(ctx, previewX, previewY, previewW, previewH);
        ctx.drawText(this.textRenderer, Text.literal("Preview"), previewX, previewY - 14, COLOR_TEXT_DIM, true);

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            int entitySize = Math.max(40, Math.min(previewW, previewH) / 3);
            InventoryScreen.drawEntity(ctx,
                    previewX + 10, previewY + 10,
                    previewX + previewW - 10, previewY + previewH - 10,
                    entitySize, 0.0625f,
                    mouseX, mouseY, mc.player);
        }

        drawColoredButton(ctx, cancelX, cancelY, BTN_W, BTN_H, "Cancelar",
                COLOR_BTN_RED, COLOR_BTN_RED_HOVER, mouseX, mouseY);
        drawColoredButton(ctx, confirmX, confirmY, BTN_W, BTN_H, "Confirmar",
                COLOR_BTN_GREEN, COLOR_BTN_GREEN_HOVER, mouseX, mouseY);
    }

    private <T> void renderList(DrawContext ctx, int mouseX, int mouseY,
                                T[] values, java.util.function.Function<T, String> label, int selectedOrd) {
        int currentHoverRow = -1;
        for (int i = 0; i < values.length; i++) {
            int ry = listY + 6 + i * LIST_ROW_H;
            boolean isSelected = i == selectedOrd;
            boolean isHover = mouseX >= listX + 4 && mouseX < listX + listW - 4
                           && mouseY >= ry && mouseY < ry + LIST_ROW_H;
            if (isHover) {
                currentHoverRow = i;
            }
            if (isSelected) {
                ctx.fill(listX + 4, ry, listX + listW - 4, ry + LIST_ROW_H, COLOR_ROW_SEL);
            } else if (isHover) {
                ctx.fill(listX + 4, ry, listX + listW - 4, ry + LIST_ROW_H, COLOR_ROW_HOVER);
            }
            int textColor = isSelected ? COLOR_TEXT_SEL : COLOR_TEXT;
            ctx.drawText(this.textRenderer, Text.literal(label.apply(values[i])),
                    listX + 16, ry + 5, textColor, true);
        }

        if (currentHoverRow >= 0 && currentHoverRow != hoveredListRow) {
            playUiSound(ModSounds.UI_MOUSE_HOVER);
        }
        hoveredListRow = currentHoverRow;
    }

    private void drawPanel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x - 2, y - 2, x + w + 2, y + h + 2, COLOR_BORDER);
        ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, COLOR_PANEL_FRAME);
        ctx.fill(x, y, x + w, y + h, COLOR_PANEL_BG);
    }

    private void drawFieldLabel(DrawContext ctx, int x, int y, String label) {
        ctx.drawText(this.textRenderer, Text.literal(label), x, y + 4, COLOR_TEXT_DIM, true);
    }

    private void drawClickableField(DrawContext ctx, int x, int y, int w, int h,
                                    String value, int color, boolean active, boolean hover) {
        int bg = (active || hover) ? COLOR_FIELD_HOT : COLOR_FIELD_BG;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.drawBorder(x, y, w, h, active ? COLOR_TEXT_ACCENT : COLOR_PANEL_FRAME);
        ctx.drawText(this.textRenderer, Text.literal(value), x + 4, y + 4, color, true);
    }

    private void drawStatRow(DrawContext ctx, int x, int y, Identifier icon, String label, int value) {
        ctx.drawTexture(icon, x, y, ICON_SIZE, ICON_SIZE, 0f, 0f, 30, 30, 30, 30);
        ctx.drawText(this.textRenderer, Text.literal(label), x + ICON_SIZE + 6, y + 3, COLOR_TEXT, true);
        String v = Integer.toString(value);
        int vw = this.textRenderer.getWidth(v);
        ctx.drawText(this.textRenderer, Text.literal(v), x + 210 - vw, y + 3, COLOR_TEXT_ACCENT, true);
    }

    private void drawColoredButton(DrawContext ctx, int x, int y, int w, int h,
                                   String label, int base, int hover, int mouseX, int mouseY) {
        boolean isHover = inRect(mouseX, mouseY, x, y, w, h);
        ctx.fill(x - 1, y - 1, x + w + 1, y + h + 1, COLOR_BTN_FRAME);
        ctx.fill(x, y, x + w, y + h, isHover ? hover : base);
        ctx.fill(x, y, x + w, y + 1, 0x33FFFFFF);
        int textW = this.textRenderer.getWidth(label);
        ctx.drawText(this.textRenderer, Text.literal(label),
                x + (w - textW) / 2, y + (h - this.textRenderer.fontHeight) / 2 + 1,
                0xFFFFFFFF, true);
    }

    private static void playUiSound(SoundEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        client.getSoundManager().play(PositionedSoundInstance.master(event, 1.0f));
    }
}
