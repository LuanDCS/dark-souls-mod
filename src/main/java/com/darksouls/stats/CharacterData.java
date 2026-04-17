package com.darksouls.stats;

import net.minecraft.nbt.NbtCompound;

/**
 * Mutable bag of per-player character data. Kept deliberately plain — derived
 * values (max HP, max stamina, future defenses) live in {@link StatRules} and
 * are recomputed on demand, never persisted.
 */
public final class CharacterData {
    public boolean created = false;
    public String name = "";
    public CharacterClass characterClass = CharacterClass.KNIGHT;
    public Gift gift = Gift.NONE;
    public int level = CharacterClass.KNIGHT.level;
    public int vitality     = CharacterClass.KNIGHT.vitality;
    public int attunement   = CharacterClass.KNIGHT.attunement;
    public int endurance    = CharacterClass.KNIGHT.endurance;
    public int strength     = CharacterClass.KNIGHT.strength;
    public int dexterity    = CharacterClass.KNIGHT.dexterity;
    public int resistance   = CharacterClass.KNIGHT.resistance;
    public int intelligence = CharacterClass.KNIGHT.intelligence;
    public int faith        = CharacterClass.KNIGHT.faith;
    public int humanity = 0;

    /** Wipe back to "uncreated" defaults. Used when loading into a world with no saved data. */
    public void reset() {
        created = false;
        name = "";
        characterClass = CharacterClass.KNIGHT;
        gift = Gift.NONE;
        level        = CharacterClass.KNIGHT.level;
        vitality     = CharacterClass.KNIGHT.vitality;
        attunement   = CharacterClass.KNIGHT.attunement;
        endurance    = CharacterClass.KNIGHT.endurance;
        strength     = CharacterClass.KNIGHT.strength;
        dexterity    = CharacterClass.KNIGHT.dexterity;
        resistance   = CharacterClass.KNIGHT.resistance;
        intelligence = CharacterClass.KNIGHT.intelligence;
        faith        = CharacterClass.KNIGHT.faith;
        humanity = 0;
    }

    public void applyClassDefaults(CharacterClass cls) {
        this.characterClass = cls;
        this.level        = cls.level;
        this.vitality     = cls.vitality;
        this.attunement   = cls.attunement;
        this.endurance    = cls.endurance;
        this.strength     = cls.strength;
        this.dexterity    = cls.dexterity;
        this.resistance   = cls.resistance;
        this.intelligence = cls.intelligence;
        this.faith        = cls.faith;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("created", created);
        nbt.putString("name", name);
        nbt.putInt("class", characterClass.ordinal());
        nbt.putInt("gift", gift.ordinal());
        nbt.putInt("level", level);
        nbt.putInt("vit", vitality);
        nbt.putInt("att", attunement);
        nbt.putInt("end", endurance);
        nbt.putInt("str", strength);
        nbt.putInt("dex", dexterity);
        nbt.putInt("res", resistance);
        nbt.putInt("int", intelligence);
        nbt.putInt("fth", faith);
        nbt.putInt("humanity", humanity);
    }

    public void readNbt(NbtCompound nbt) {
        created = nbt.getBoolean("created");
        name = nbt.getString("name");
        characterClass = CharacterClass.fromOrdinal(nbt.getInt("class"));
        gift = Gift.fromOrdinal(nbt.getInt("gift"));
        level        = nbt.getInt("level");
        vitality     = nbt.getInt("vit");
        attunement   = nbt.getInt("att");
        endurance    = nbt.getInt("end");
        strength     = nbt.getInt("str");
        dexterity    = nbt.getInt("dex");
        resistance   = nbt.getInt("res");
        intelligence = nbt.getInt("int");
        faith        = nbt.getInt("fth");
        humanity     = nbt.getInt("humanity");
    }
}
