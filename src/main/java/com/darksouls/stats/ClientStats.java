package com.darksouls.stats;

import com.darksouls.network.CharacterSyncPayload;

/**
 * Client-side mirror of server state that drives the HUD and the creation screen.
 *
 * HP is read directly from the player entity (attributes already synced);
 * everything else below is pushed from the server via network payloads.
 */
public final class ClientStats {
    public static float stamina = 100.0f;
    public static float staminaMax = 100.0f;

    public static final CharacterData character = new CharacterData();

    private ClientStats() {}

    public static int humanity() {
        return character.humanity;
    }

    public static boolean characterCreated() {
        return character.created;
    }

    public static float staminaFraction() {
        return staminaMax <= 0 ? 0f : Math.max(0f, Math.min(1f, stamina / staminaMax));
    }

    /** Reset local mirror when disconnecting so a new world starts clean. */
    public static void reset() {
        stamina = 100.0f;
        staminaMax = 100.0f;
        character.reset();
    }

    public static void applySync(CharacterSyncPayload p) {
        character.created = p.created();
        character.name = p.name();
        character.characterClass = CharacterClass.fromOrdinal(p.classOrd());
        character.gift = Gift.fromOrdinal(p.giftOrd());
        character.level        = p.level();
        character.vitality     = p.vitality();
        character.attunement   = p.attunement();
        character.endurance    = p.endurance();
        character.strength     = p.strength();
        character.dexterity    = p.dexterity();
        character.resistance   = p.resistance();
        character.intelligence = p.intelligence();
        character.faith        = p.faith();
        character.humanity     = p.humanity();
    }
}
