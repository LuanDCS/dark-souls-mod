package com.darksouls.mixin;

import com.darksouls.stats.StaminaManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttackMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private void darksouls$drainOnAttack(Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self instanceof ServerPlayerEntity server) {
            StaminaManager.drain(server, StaminaManager.ATTACK_DRAIN);
        }
    }
}
