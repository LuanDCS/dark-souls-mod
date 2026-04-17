package com.darksouls.bonfire;

import com.darksouls.block.BonfireBlock;
import com.darksouls.block.ModBlocks;
import com.darksouls.network.BonfireEventPayload;
import com.darksouls.network.ModNetworking;
import com.darksouls.stats.StaminaManager;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Server-side authority for bonfire interactions.
 *
 * Rules:
 * - First interaction on an unlit bonfire lights it and sets respawn.
 * - Interacting with an already lit bonfire performs "rest":
 *   heal, refill stamina, set respawn, then trigger client transition/menu.
 */
public final class BonfireManager {
    private static final double MAX_INTERACT_DISTANCE_SQ = 25.0; // 5 blocks

    private BonfireManager() {}

    public static void handleInteract(ServerPlayerEntity player, BlockPos pos) {
        if (player == null || pos == null) return;

        ServerWorld world = player.getServerWorld();
        if (player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > MAX_INTERACT_DISTANCE_SQ) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        if (!state.isOf(ModBlocks.BONFIRE)) {
            return;
        }

        boolean lit = state.contains(BonfireBlock.LIT) && state.get(BonfireBlock.LIT);
        if (!lit) {
            activate(player, world, pos, state);
        } else {
            rest(player, pos);
        }
    }

    private static void activate(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(BonfireBlock.LIT, true), 3);
        setRespawn(player, pos);
        ModNetworking.sendBonfireEvent(player, BonfireEventPayload.activated());
    }

    private static void rest(ServerPlayerEntity player, BlockPos pos) {
        setRespawn(player, pos);
        player.setHealth(player.getMaxHealth());
        player.getHungerManager().setFoodLevel(20);
        StaminaManager.restoreToMax(player);
        ModNetworking.sendBonfireEvent(player, BonfireEventPayload.restStart());
    }

    private static void setRespawn(ServerPlayerEntity player, BlockPos bonfirePos) {
        BlockPos respawnPos = bonfirePos.up();
        player.setSpawnPoint(player.getWorld().getRegistryKey(), respawnPos, 0.0f, true, false);
    }
}
