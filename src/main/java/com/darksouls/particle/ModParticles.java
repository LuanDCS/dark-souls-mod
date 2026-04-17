package com.darksouls.particle;

import com.darksouls.DarkSoulsMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * Central registration for custom particle types.
 */
public final class ModParticles {
    public static final SimpleParticleType BONFIRE_SMOKE = Registry.register(
            Registries.PARTICLE_TYPE,
            DarkSoulsMod.id("bonfire_smoke"),
            FabricParticleTypes.simple()
    );

    private ModParticles() {}

    public static void init() {
        // no-op; class-load side effect registers static fields.
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ParticleFactoryRegistry.getInstance().register(BONFIRE_SMOKE, BonfireSmokeParticle.Factory::new);
    }
}
