package com.darksouls.stats;

import com.darksouls.network.ModNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-authoritative stamina store.
 *
 * Invariants:
 *   - 0 <= current <= max always (enforced by {@link StaminaState#setCurrent}).
 *   - Every mutation that changes {@code current} emits a sync payload, so the
 *     client HUD never drifts from the server value.
 *   - Regen is gated by a cooldown in ticks; any drain resets it.
 *
 * Future hooks (weapon weight, equipment load) should feed into {@code drain()}
 * amounts and the dynamic {@code regenPerTick()} / {@code sprintDrainPerTick()}
 * instead of being scattered across call sites.
 */
public final class StaminaManager {
    public static final float DEFAULT_MAX = 100.0f;
    public static final float SPRINT_DRAIN_PER_TICK = 0.9f;
    public static final float ATTACK_DRAIN = 20.0f;
    public static final float REGEN_PER_TICK = 1.4f;
    /** Ticks the player must NOT be draining before regen resumes. 2 seconds at 20 tps. */
    public static final int REGEN_DELAY_TICKS = 40;

    private static final Map<UUID, StaminaState> STATES = new HashMap<>();

    private StaminaManager() {}

    public static StaminaState get(ServerPlayerEntity player) {
        StaminaState s = STATES.computeIfAbsent(player.getUuid(), u -> new StaminaState(DEFAULT_MAX));
        CharacterData cd = CharacterManager.get(player);
        float desiredMax = cd.created ? StatRules.maxStamina(cd.endurance) : DEFAULT_MAX;
        if (s.max != desiredMax) {
            s.max = desiredMax;
            if (s.current > s.max) s.current = s.max;
        }
        return s;
    }

    public static void drain(ServerPlayerEntity player, float amount) {
        if (amount <= 0f) return;
        StaminaState s = get(player);
        if (s.setCurrent(s.current - amount)) {
            s.regenCooldown = REGEN_DELAY_TICKS;
            ModNetworking.sendStamina(player, s);
        } else {
            s.regenCooldown = REGEN_DELAY_TICKS;
        }
    }

    public static void remove(UUID uuid) {
        STATES.remove(uuid);
    }

    public static void tickAll(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            tickPlayer(player);
        }
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        StaminaState s = get(player);
        float before = s.current;

        boolean sprinting = player.isSprinting();

        if (sprinting && s.current > 0f) {
            s.setCurrent(s.current - sprintDrainPerTick(player));
            s.regenCooldown = REGEN_DELAY_TICKS;
            if (s.current <= 0f) {
                player.setSprinting(false);
            }
        } else if (sprinting) {
            // Out of stamina but the client is still requesting sprint — keep the
            // cooldown pinned and force-cancel sprint until the key is released.
            player.setSprinting(false);
            s.regenCooldown = REGEN_DELAY_TICKS;
        } else if (s.regenCooldown > 0) {
            s.regenCooldown--;
        } else if (s.current < s.max) {
            s.setCurrent(s.current + regenPerTick(player));
        }

        if (s.current != before) {
            ModNetworking.sendStamina(player, s);
        }
    }

    private static float sprintDrainPerTick(ServerPlayerEntity player) {
        // Placeholder for equipment-weight scaling. Keep the hook even though the
        // current value is constant, so the stats system has a single seam.
        return SPRINT_DRAIN_PER_TICK;
    }

    private static float regenPerTick(ServerPlayerEntity player) {
        // Same rationale as above — regen will later scale with equip load.
        return REGEN_PER_TICK;
    }

    public static final class StaminaState {
        public float current;
        public float max;
        public int regenCooldown;

        public StaminaState(float max) {
            this.max = max;
            this.current = max;
        }

        /** Clamps {@code value} into [0, max] and writes it. Returns true if the stored value changed. */
        public boolean setCurrent(float value) {
            float clamped = value < 0f ? 0f : (value > max ? max : value);
            if (clamped == current) return false;
            current = clamped;
            return true;
        }
    }
}
