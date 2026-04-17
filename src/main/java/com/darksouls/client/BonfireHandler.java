package com.darksouls.client;

import com.darksouls.block.ModBlocks;
import com.darksouls.network.ModNetworking;
import com.darksouls.screen.BonfireScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side bonfire interaction:
 *  - Scans for a nearby bonfire each tick.
 *  - Shows "Pressione E para descansar" on the action bar while in range.
 *  - Opens the {@link BonfireScreen} when E is pressed while in range.
 *
 * Uses raw GLFW polling rather than a KeyBinding so we can detect E
 * independently of the vanilla inventory binding.
 */
public final class BonfireHandler {
    private static final double DETECTION_RADIUS = 4.0;
    private static final int SCAN_RADIUS_BLOCKS = 5;

    private static boolean prevEPressed = false;

    private BonfireHandler() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BonfireHandler::onTick);
    }

    private static void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            prevEPressed = false;
            return;
        }

        BlockPos nearby = findNearbyBonfire(client.world, player.getBlockPos());
        boolean inRange = nearby != null;

        if (inRange && client.currentScreen == null) {
            client.inGameHud.setOverlayMessage(Text.literal("Pressione E para descansar"), false);
        }

        boolean ePressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_E);
        boolean risingEdge = ePressed && !prevEPressed;
        prevEPressed = ePressed;

        if (risingEdge && inRange) {
            // If vanilla inventory key fired simultaneously, replace it with the bonfire screen.
            if (client.currentScreen == null || client.currentScreen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen) {
                client.setScreen(new BonfireScreen());
                ModNetworking.sendRestAtBonfire();
            }
        }
    }

    private static BlockPos findNearbyBonfire(World world, BlockPos center) {
        int r = SCAN_RADIUS_BLOCKS;
        double bestSq = DETECTION_RADIUS * DETECTION_RADIUS;
        BlockPos best = null;
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (world.getBlockState(pos).isOf(ModBlocks.BONFIRE)) {
                        double distSq = center.getSquaredDistance(pos);
                        if (distSq <= bestSq) {
                            bestSq = distSq;
                            best = pos.toImmutable();
                        }
                    }
                }
            }
        }
        return best;
    }
}
