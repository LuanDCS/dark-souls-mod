package com.darksouls.client.sound;

import com.darksouls.client.BonfireHandler;
import com.darksouls.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Looping bonfire ambience that follows the nearest lit bonfire and fades with distance.
 */
public final class BonfireAmbientSoundInstance extends MovingSoundInstance {
    private static final double MAX_AUDIBLE_DISTANCE = 20.0;
    private static final int SCAN_RADIUS_BLOCKS = 20;

    private final MinecraftClient client;

    public BonfireAmbientSoundInstance(MinecraftClient client, BlockPos initialBonfirePos) {
        super(ModSounds.BONFIRE_AMBIENT, SoundCategory.BLOCKS, SoundInstance.createRandom());
        this.client = client;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.35f;
        this.pitch = 1.0f;

        if (initialBonfirePos != null) {
            Vec3d center = Vec3d.ofCenter(initialBonfirePos);
            this.x = center.x;
            this.y = center.y;
            this.z = center.z;
        }
    }

    @Override
    public void tick() {
        if (client == null || client.player == null || client.world == null) {
            this.setDone();
            return;
        }

        BlockPos nearest = BonfireHandler.findNearbyLitBonfire(
                client.world,
                client.player.getBlockPos(),
                MAX_AUDIBLE_DISTANCE,
                SCAN_RADIUS_BLOCKS
        );
        if (nearest == null) {
            this.setDone();
            return;
        }

        Vec3d center = Vec3d.ofCenter(nearest);
        this.x = center.x;
        this.y = center.y;
        this.z = center.z;

        double dist = client.player.getPos().distanceTo(center);
        float distanceFactor = (float) (1.0 - (dist / MAX_AUDIBLE_DISTANCE));
        if (distanceFactor < 0.0f) distanceFactor = 0.0f;
        if (distanceFactor > 1.0f) distanceFactor = 1.0f;
        this.volume = Math.max(0.02f, distanceFactor * 0.95f);
    }

    public void stop() {
        this.setDone();
    }
}
