package com.darksouls.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fully disable the hunger mechanic: skip the tick update and veto exhaustion.
 * The vanilla food bar is also hidden by {@link com.darksouls.mixin.client.InGameHudMixin}.
 */
@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void darksouls$skipHungerUpdate(PlayerEntity player, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void darksouls$skipExhaustion(float amount, CallbackInfo ci) {
        ci.cancel();
    }
}
