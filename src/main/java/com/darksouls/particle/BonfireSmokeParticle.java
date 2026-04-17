package com.darksouls.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/**
 * Upward, soft, expanding smoke particle with gentle horizontal drift.
 */
public final class BonfireSmokeParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    private final float startScale;
    private final float endScale;
    private final float startAlpha;

    private BonfireSmokeParticle(ClientWorld world, double x, double y, double z,
                                 double velocityX, double velocityY, double velocityZ,
                                 SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.gravityStrength = 0.0f;
        this.collidesWithWorld = false;

        this.maxAge = 40 + this.random.nextInt(41); // 40..80 ticks
        this.startScale = 0.6f + this.random.nextFloat() * 0.4f; // 0.6..1.0
        this.endScale = 1.5f + this.random.nextFloat() * 0.7f;   // 1.5..2.2
        this.startAlpha = 0.8f + this.random.nextFloat() * 0.2f; // 0.8..1.0
        this.scale = this.startScale;
        this.alpha = this.startAlpha;

        // Warm grayscale so the base can feel slightly heated without looking orange everywhere.
        float warmth = 0.78f + this.random.nextFloat() * 0.12f;
        this.setColor(warmth, warmth * 0.93f, warmth * 0.86f);

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        // Slight stochastic drift to avoid rigid, straight columns.
        this.velocityX = (this.velocityX + (this.random.nextDouble() - 0.5) * 0.0012) * 0.987;
        this.velocityZ = (this.velocityZ + (this.random.nextDouble() - 0.5) * 0.0012) * 0.987;

        // Keep upward motion smooth and limited to the intended range.
        this.velocityY = Math.min(0.06, Math.max(0.02, this.velocityY + 0.00035));

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        float t = this.age / (float) this.maxAge;
        this.scale = lerp(t, this.startScale, this.endScale);
        this.alpha = this.startAlpha * (1.0f - t);

        this.setSpriteForAge(this.spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    private static float lerp(float t, float a, float b) {
        return a + (b - a) * t;
    }

    public static final class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new BonfireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        }
    }
}
