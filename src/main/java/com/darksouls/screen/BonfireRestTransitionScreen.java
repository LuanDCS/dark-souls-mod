package com.darksouls.screen;

import com.darksouls.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;

/**
 * Pre-menu smoke transition shown when the player rests at a lit bonfire.
 */
public final class BonfireRestTransitionScreen extends Screen {
    private static final int DEFAULT_TICKS = 46;
    private static final float MENU_OPEN_PROGRESS = 0.74f;

    private final int durationTicks;
    private int ticksLeft;
    private int age;
    private boolean menuOpened;

    public BonfireRestTransitionScreen(int durationTicks) {
        super(Text.literal("Bonfire Rest Transition"));
        this.durationTicks = durationTicks <= 0 ? DEFAULT_TICKS : durationTicks;
        this.ticksLeft = this.durationTicks;
    }

    @Override
    protected void init() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null) {
            client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.BONFIRE_SIT, 1.0f));
        }
        BonfireSmokeOverlayRenderer.startOverlay();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        age++;
        ticksLeft--;
        float progress = 1.0f - (ticksLeft / (float) durationTicks);

        if (!menuOpened && progress >= MENU_OPEN_PROGRESS) {
            menuOpened = true;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.setScreen(new BonfireScreen());
            }
            return;
        }

        if (ticksLeft <= 0) {
            BonfireSmokeOverlayRenderer.stopOverlay();
            this.close();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        BonfireSmokeOverlayRenderer.render(ctx, this.width, this.height, true);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return true;
    }
}
