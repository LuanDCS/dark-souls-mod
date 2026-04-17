package com.darksouls.stats;

import com.darksouls.network.CharacterSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-authoritative store for the per-player {@link CharacterData}.
 *
 * NBT read/write is driven by {@code PlayerEntityNbtMixin}; this class owns the
 * live map and the plumbing that re-applies derived values (max HP, max
 * stamina) to the {@link ServerPlayerEntity} whenever the data changes.
 */
public final class CharacterManager {
    private static final Map<UUID, CharacterData> STATES = new HashMap<>();

    private CharacterManager() {}

    public static CharacterData get(ServerPlayerEntity player) {
        return STATES.computeIfAbsent(player.getUuid(), u -> new CharacterData());
    }

    public static CharacterData getByUuid(UUID uuid) {
        return STATES.computeIfAbsent(uuid, u -> new CharacterData());
    }

    public static void remove(UUID uuid) {
        STATES.remove(uuid);
    }

    /** Drop all in-memory character data. Called when the server stops so a fresh
     *  world (same JVM session) does not inherit stale state. */
    public static void clearAll() {
        STATES.clear();
    }

    /**
     * Applies derived values from the player's {@link CharacterData} onto the
     * live entity: max HP goes into the attribute, stamina max is refreshed via
     * the next {@code StaminaManager} read.
     */
    public static void applyToPlayer(ServerPlayerEntity player) {
        CharacterData data = get(player);
        if (!data.created) return;

        EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHealth != null) {
            float hp = StatRules.maxHp(data.vitality);
            if (maxHealth.getBaseValue() != hp) {
                maxHealth.setBaseValue(hp);
            }
        }

        // Stamina state is pulled lazily by StaminaManager.get(), which reads the
        // current endurance every tick — no push needed here.
    }

    /**
     * First-time creation: copies class defaults, flags the player as created,
     * applies attributes, heals to full, and syncs.
     */
    public static boolean tryCreate(ServerPlayerEntity player, String name, CharacterClass cls, Gift gift) {
        CharacterData data = get(player);
        if (data.created) return false;

        data.name = name == null ? "" : name.trim();
        data.applyClassDefaults(cls);
        data.gift = gift == null ? Gift.NONE : gift;
        data.humanity = 0;
        data.created = true;

        applyToPlayer(player);
        player.setHealth(player.getMaxHealth());
        syncToClient(player);
        return true;
    }

    public static void syncToClient(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, CharacterSyncPayload.from(get(player)));
    }
}
