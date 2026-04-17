package com.darksouls.mixin;

import com.darksouls.stats.CharacterData;
import com.darksouls.stats.CharacterManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Persist {@link com.darksouls.stats.CharacterData} alongside the vanilla player
 * save, using a single sub-compound under the mod namespace so we never clash
 * with vanilla or other mod keys.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityNbtMixin {
    private static final String KEY = "darksouls.character";

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void darksouls$writeCharacter(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self instanceof ServerPlayerEntity sp) {
            NbtCompound sub = new NbtCompound();
            CharacterManager.get(sp).writeNbt(sub);
            nbt.put(KEY, sub);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void darksouls$readCharacter(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self instanceof ServerPlayerEntity sp) {
            CharacterData data = CharacterManager.get(sp);
            if (nbt.contains(KEY)) {
                data.readNbt(nbt.getCompound(KEY));
            } else {
                // Fresh world or pre-mod save — wipe any state lingering in the
                // in-memory map from a previous world in the same JVM session.
                data.reset();
            }
        }
    }
}
