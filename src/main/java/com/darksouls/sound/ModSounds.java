package com.darksouls.sound;

import com.darksouls.DarkSoulsMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Central registry of mod sound events. Loaded both client- and server-side so
 * any mod code can reference {@code ModSounds.UI_MENU_OPEN} safely, even though
 * only the client actually plays UI sounds.
 */
public final class ModSounds {
    public static final SoundEvent UI_MOUSE_HOVER = register("ui.mouse_hover");
    public static final SoundEvent UI_MENU_OPEN   = register("ui.menu_open");
    public static final SoundEvent UI_MENU_BACK   = register("ui.menu_back");
    public static final SoundEvent UI_MOUSEMENU   = register("ui.mousemenu");
<<<<<<< HEAD
=======
    public static final SoundEvent BONFIRE_AMBIENT = register("bonfire.ambient");
    public static final SoundEvent BONFIRE_SIT     = register("bonfire.sit");
>>>>>>> ba47194 (Melhora no sistema de BonFire)

    private ModSounds() {}

    public static void init() {
        // Force class-load so the static fields register before anything plays.
    }

    private static SoundEvent register(String path) {
        Identifier id = DarkSoulsMod.id(path);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
