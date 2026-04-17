package com.darksouls.stats;

/**
 * Ten starting classes from Dark Souls 1, with the exact soul level and base
 * stats each one begins with. Names are kept in English on purpose (per spec):
 * only {@code Attunement} is surfaced in Portuguese in the UI as "Conhecimento".
 *
 * Stat order: level, vitality, attunement, endurance, strength, dexterity,
 * resistance, intelligence, faith.
 */
public enum CharacterClass {
    WARRIOR   ("Warrior",    4, 11,  8, 12, 13, 13, 11,  9,  9),
    KNIGHT    ("Knight",     5, 14, 10, 10, 11, 11, 10,  9, 11),
    WANDERER  ("Wanderer",   3, 10, 11, 10, 10, 14, 12, 11,  8),
    THIEF     ("Thief",      5,  9, 11,  9,  9, 15, 10, 12, 11),
    BANDIT    ("Bandit",     4, 12,  8, 14, 14,  9, 11,  8, 10),
    HUNTER    ("Hunter",     4, 11,  9, 11, 12, 14, 11,  9,  9),
    SORCERER  ("Sorcerer",   3,  8, 15,  8,  9, 11,  8, 15,  8),
    PYROMANCER("Pyromancer", 1, 10, 12, 11, 12,  9, 12, 10,  8),
    CLERIC    ("Cleric",     2, 11, 11,  9, 12,  8, 11,  8, 14),
    DEPRIVED  ("Deprived",   6, 11, 11, 11, 11, 11, 11, 11, 11);

    public final String displayName;
    public final int level;
    public final int vitality;
    public final int attunement;
    public final int endurance;
    public final int strength;
    public final int dexterity;
    public final int resistance;
    public final int intelligence;
    public final int faith;

    CharacterClass(String displayName, int level,
                   int vitality, int attunement, int endurance,
                   int strength, int dexterity, int resistance,
                   int intelligence, int faith) {
        this.displayName = displayName;
        this.level = level;
        this.vitality = vitality;
        this.attunement = attunement;
        this.endurance = endurance;
        this.strength = strength;
        this.dexterity = dexterity;
        this.resistance = resistance;
        this.intelligence = intelligence;
        this.faith = faith;
    }

    public static CharacterClass fromOrdinal(int ord) {
        CharacterClass[] values = values();
        return (ord < 0 || ord >= values.length) ? KNIGHT : values[ord];
    }
}
