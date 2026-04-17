package com.darksouls.stats;

/**
 * Pure functions that derive gameplay values from base attributes.
 *
 * Every curve is based on DS1 behaviour, scaled to Minecraft's HP/stamina units:
 *
 *   Vitality -> Max HP
 *     Piecewise linear with soft caps at 30 and 50, matching the DS1 diminishing
 *     returns. Output is Minecraft HP (2 HP = 1 heart).
 *
 *   Endurance -> Max Stamina
 *     Linear from 80 at 0 Endurance to 160 at 40 Endurance, flat afterwards
 *     (hard cap at 40 — DS1 stamina stops growing there).
 *
 * Soft caps for Strength/Dexterity/Int/Faith are encoded as helpers so the
 * future combat/magic code reads them from here and not from scattered magic
 * numbers.
 */
public final class StatRules {
    public static final int ATTR_MIN = 1;
    public static final int ATTR_MAX = 99;

    public static final int VIT_SOFT_CAP_1 = 30;
    public static final int VIT_SOFT_CAP_2 = 50;
    public static final int END_HARD_CAP   = 40;

    private StatRules() {}

    public static int clampAttr(int v) {
        return v < ATTR_MIN ? ATTR_MIN : (v > ATTR_MAX ? ATTR_MAX : v);
    }

    public static float maxHp(int vitality) {
        int v = clampAttr(vitality);
        float hp = 16f;
        int seg1 = Math.min(v, VIT_SOFT_CAP_1) - 1;
        hp += seg1 * 1.2f;
        if (v > VIT_SOFT_CAP_1) {
            int seg2 = Math.min(v, VIT_SOFT_CAP_2) - VIT_SOFT_CAP_1;
            hp += seg2 * 0.5f;
        }
        if (v > VIT_SOFT_CAP_2) {
            int seg3 = v - VIT_SOFT_CAP_2;
            hp += seg3 * 0.2f;
        }
        return hp;
    }

    public static float maxStamina(int endurance) {
        int e = Math.max(0, Math.min(endurance, END_HARD_CAP));
        return 80f + 2f * e;
    }

    /** Extra GUI width, in pixels, to add to the HP bar at this Vitality. */
    public static int hpBarBonusWidth(int vitality) {
        return Math.max(0, (clampAttr(vitality) - 10)) * 2;
    }

    /** Extra GUI width, in pixels, to add to the stamina bar at this Endurance. */
    public static int staminaBarBonusWidth(int endurance) {
        return Math.max(0, Math.min(endurance, END_HARD_CAP) - 10) * 2;
    }
}
