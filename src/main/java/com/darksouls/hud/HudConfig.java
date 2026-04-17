package com.darksouls.hud;

/**
 * Single source of truth for HUD dimensions.
 *
 * These values are intentionally mutable so the future attribute system can
 * grow the bars as the player levels up (Vigor -> wider HP, Endurance -> wider
 * stamina). When that system lands, it should write into this class — the HUD
 * renderer already reads every dimension from here on each frame.
 *
 * Pixel values are in GUI (post-scale) units.
 */
public final class HudConfig {
    /** Base HP bar width before any attribute bonuses. */
    public static int hpBarBaseWidth   = 190;
    public static int hpBarBonusWidth  = 0;
    public static int hpBarHeight      = 9;

    /** Stamina is visually narrower and thinner than HP, matching DS1. */
    public static int staminaBarBaseWidth  = 150;
    public static int staminaBarBonusWidth = 0;
    public static int staminaBarHeight     = 6;

    public static int barGap        = 3;
    public static int marginX       = 14;
    public static int marginY       = 14;
    public static int circleSize    = 28;
    public static int circleGap     = 6;

    private HudConfig() {}

    public static int hpBarWidth() {
        return hpBarBaseWidth + hpBarBonusWidth;
    }

    public static int staminaBarWidth() {
        return staminaBarBaseWidth + staminaBarBonusWidth;
    }
}
