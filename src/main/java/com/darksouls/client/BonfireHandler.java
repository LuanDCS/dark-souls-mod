package com.darksouls.client;

<<<<<<< HEAD
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
=======
import com.darksouls.block.BonfireBlock;
import com.darksouls.block.ModBlocks;
import com.darksouls.client.sound.BonfireAmbientSoundInstance;
import com.darksouls.network.BonfireEventPayload;
import com.darksouls.network.ModNetworking;
import com.darksouls.particle.ModParticles;
import com.darksouls.screen.BonfireLitScreen;
import com.darksouls.screen.BonfireRestTransitionScreen;
import com.darksouls.screen.BonfireSmokeOverlayRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;

/**
 * Client-side bonfire orchestrator:
 * - Proximity prompts
 * - Interaction key handling
 * - Ambient bonfire loop management
 * - UI reactions to server-side bonfire events
 */
public final class BonfireHandler {
    private static final double INTERACT_RADIUS = 4.0;
    private static final int INTERACT_SCAN_RADIUS = 5;
    private static final double AMBIENT_RADIUS = 20.0;
    private static final int AMBIENT_SCAN_RADIUS = 20;
    private static final double SMOKE_RADIUS = 14.0;
    private static final int SMOKE_SCAN_RADIUS = 14;
    private static final int MAX_ACTIVE_SMOKE = 50;
    private static final int MIN_SMOKE_LIFETIME = 40;
    private static final int MAX_SMOKE_LIFETIME = 80;

    private static boolean prevEPressed = false;
    private static BonfireAmbientSoundInstance ambientSound = null;
    private static float smokeSpawnAccumulator = 0.0f;
    private static long clientTickCounter = 0L;
    private static final ArrayDeque<Long> smokeExpiryTicks = new ArrayDeque<>();
>>>>>>> ba47194 (Melhora no sistema de BonFire)

    private BonfireHandler() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(BonfireHandler::onTick);
    }

<<<<<<< HEAD
=======
    public static void handleServerEvent(BonfireEventPayload payload) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        client.execute(() -> {
            if (payload.eventType() == BonfireEventPayload.EVENT_ACTIVATED) {
                client.setScreen(new BonfireLitScreen());
            } else if (payload.eventType() == BonfireEventPayload.EVENT_REST_START) {
                client.setScreen(new BonfireRestTransitionScreen(payload.transitionTicks()));
            }
        });
    }

>>>>>>> ba47194 (Melhora no sistema de BonFire)
    private static void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            prevEPressed = false;
<<<<<<< HEAD
            return;
        }

        BlockPos nearby = findNearbyBonfire(client.world, player.getBlockPos());
        boolean inRange = nearby != null;

        if (inRange && client.currentScreen == null) {
            client.inGameHud.setOverlayMessage(Text.literal("Pressione E para descansar"), false);
=======
            stopAmbient();
            smokeSpawnAccumulator = 0.0f;
            smokeExpiryTicks.clear();
            BonfireSmokeOverlayRenderer.reset();
            return;
        }

        clientTickCounter++;
        updateAmbient(client, player);
        updateSmoke(client, player);

        BlockPos nearby = findNearbyBonfire(client.world, player.getBlockPos(), INTERACT_RADIUS, INTERACT_SCAN_RADIUS);
        boolean inRange = nearby != null;

        if (inRange && client.currentScreen == null) {
            boolean lit = isBonfireLit(client.world.getBlockState(nearby));
            String prompt = lit
                    ? "Pressione E para descansar"
                    : "Pressione E para acender bonfire";
            client.inGameHud.setOverlayMessage(Text.literal(prompt), false);
>>>>>>> ba47194 (Melhora no sistema de BonFire)
        }

        boolean ePressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_E);
        boolean risingEdge = ePressed && !prevEPressed;
        prevEPressed = ePressed;

        if (risingEdge && inRange) {
<<<<<<< HEAD
            // If vanilla inventory key fired simultaneously, replace it with the bonfire screen.
            if (client.currentScreen == null || client.currentScreen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen) {
                client.setScreen(new BonfireScreen());
                ModNetworking.sendRestAtBonfire();
=======
            if (client.currentScreen == null || client.currentScreen instanceof InventoryScreen) {
                ModNetworking.sendBonfireInteract(nearby);
>>>>>>> ba47194 (Melhora no sistema de BonFire)
            }
        }
    }

<<<<<<< HEAD
    private static BlockPos findNearbyBonfire(World world, BlockPos center) {
        int r = SCAN_RADIUS_BLOCKS;
        double bestSq = DETECTION_RADIUS * DETECTION_RADIUS;
=======
    private static void updateAmbient(MinecraftClient client, ClientPlayerEntity player) {
        BlockPos litNearby = findNearbyLitBonfire(client.world, player.getBlockPos(), AMBIENT_RADIUS, AMBIENT_SCAN_RADIUS);
        if (litNearby != null) {
            if (ambientSound == null) {
                ambientSound = new BonfireAmbientSoundInstance(client, litNearby);
                client.getSoundManager().play(ambientSound);
            }
        } else {
            stopAmbient();
        }
    }

    private static void stopAmbient() {
        if (ambientSound != null) {
            ambientSound.stop();
            ambientSound = null;
        }
    }

    private static void updateSmoke(MinecraftClient client, ClientPlayerEntity player) {
        while (!smokeExpiryTicks.isEmpty() && smokeExpiryTicks.peekFirst() <= clientTickCounter) {
            smokeExpiryTicks.pollFirst();
        }

        BlockPos litNearby = findNearbyLitBonfire(client.world, player.getBlockPos(), SMOKE_RADIUS, SMOKE_SCAN_RADIUS);
        if (litNearby == null) {
            smokeSpawnAccumulator = 0.0f;
            return;
        }

        Random random = client.world.random;
        float spawnPerSecond = 5.0f + random.nextFloat() * 7.0f; // 5..12 / sec
        smokeSpawnAccumulator += spawnPerSecond / 20.0f;

        while (smokeSpawnAccumulator >= 1.0f && smokeExpiryTicks.size() < MAX_ACTIVE_SMOKE) {
            smokeSpawnAccumulator -= 1.0f;
            spawnSmokeParticle(client, litNearby, random);
        }
    }

    private static void spawnSmokeParticle(MinecraftClient client, BlockPos bonfirePos, Random random) {
        double x = bonfirePos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.34;
        double y = bonfirePos.getY() + 0.22 + random.nextDouble() * 0.16;
        double z = bonfirePos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.34;

        double velocityX = (random.nextDouble() - 0.5) * 0.02; // -0.01..0.01
        double velocityY = 0.02 + random.nextDouble() * 0.04;  // 0.02..0.06
        double velocityZ = (random.nextDouble() - 0.5) * 0.02; // -0.01..0.01

        client.world.addParticle(ModParticles.BONFIRE_SMOKE, x, y, z, velocityX, velocityY, velocityZ);

        int lifetime = MIN_SMOKE_LIFETIME + random.nextInt((MAX_SMOKE_LIFETIME - MIN_SMOKE_LIFETIME) + 1);
        smokeExpiryTicks.addLast(clientTickCounter + lifetime);

        // Optional warm base glow / ember hints.
        if (random.nextFloat() < 0.22f) {
            DustParticleEffect ember = new DustParticleEffect(new Vector3f(1.0f, 0.60f, 0.24f), 0.8f);
            client.world.addParticle(
                    ember,
                    bonfirePos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.20,
                    bonfirePos.getY() + 0.12,
                    bonfirePos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.20,
                    (random.nextDouble() - 0.5) * 0.01,
                    0.01 + random.nextDouble() * 0.02,
                    (random.nextDouble() - 0.5) * 0.01
            );
        }
    }

    public static BlockPos findNearbyBonfire(World world, BlockPos center, double detectionRadius, int scanRadius) {
        int r = Math.max(1, scanRadius);
        double bestSq = detectionRadius * detectionRadius;
>>>>>>> ba47194 (Melhora no sistema de BonFire)
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
<<<<<<< HEAD
=======

    public static BlockPos findNearbyLitBonfire(World world, BlockPos center, double detectionRadius, int scanRadius) {
        int r = Math.max(1, scanRadius);
        double bestSq = detectionRadius * detectionRadius;
        BlockPos best = null;
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = world.getBlockState(pos);
                    if (isBonfireLit(state)) {
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

    public static boolean isBonfireLit(BlockState state) {
        return state.isOf(ModBlocks.BONFIRE)
                && state.contains(BonfireBlock.LIT)
                && state.get(BonfireBlock.LIT);
    }
>>>>>>> ba47194 (Melhora no sistema de BonFire)
}
