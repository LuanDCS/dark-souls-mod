package com.darksouls.mixin;

import com.darksouls.stats.StaminaManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remove stamina state when the player leaves the server so the map doesn't
 * leak on long-running sessions.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityTickMixin {
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void darksouls$removeStaminaOnDisconnect(CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        StaminaManager.remove(self.getUuid());
    }
}
