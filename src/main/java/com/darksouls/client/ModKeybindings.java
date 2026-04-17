package com.darksouls.client;

import com.darksouls.screen.CharacterCreationScreen;
import com.darksouls.stats.ClientStats;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * V opens the creation screen — but only while the player does not yet have
 * a character. Once created, the keybind is a no-op, enforcing "no recreation"
 * entirely on the client side (the server also refuses duplicate creations).
 */
public final class ModKeybindings {
    private static KeyBinding openCreation;

    private ModKeybindings() {}

    public static void register() {
        openCreation = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.darksouls.open_creation",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.darksouls"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openCreation.wasPressed()) {
                tryOpenCreation(client);
            }
        });
    }

    private static void tryOpenCreation(MinecraftClient client) {
        if (client.player == null || client.currentScreen != null) return;
        if (ClientStats.characterCreated()) return;
        client.setScreen(new CharacterCreationScreen());
    }
}
