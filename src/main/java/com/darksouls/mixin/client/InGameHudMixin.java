package com.darksouls.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppress vanilla status bars (health, armor, hunger, air, mount health).
 * The custom HP bar is drawn from {@link com.darksouls.hud.DarkSoulsHud}.
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void darksouls$cancelStatusBars(DrawContext ctx, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true, require = 0)
    private void darksouls$cancelFood(DrawContext ctx, net.minecraft.entity.player.PlayerEntity player, int top, int right, CallbackInfo ci) {
        ci.cancel();
    }
}
