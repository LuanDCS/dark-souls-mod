package com.darksouls;

import com.darksouls.block.ModBlocks;
import com.darksouls.network.ModNetworking;
import com.darksouls.sound.ModSounds;
import com.darksouls.stats.CharacterManager;
import com.darksouls.stats.StaminaManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;

public final class DarkSoulsMod implements ModInitializer {
    public static final String MOD_ID = "darksouls";

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModSounds.init();
        ModBlocks.init();
        ModNetworking.registerServer();

        ServerTickEvents.END_SERVER_TICK.register(StaminaManager::tickAll);

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> CharacterManager.clearAll());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            CharacterManager.applyToPlayer(handler.getPlayer());
            CharacterManager.syncToClient(handler.getPlayer());
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            CharacterManager.applyToPlayer(newPlayer);
            newPlayer.setHealth(newPlayer.getMaxHealth());
            CharacterManager.syncToClient(newPlayer);
        });
    }
}
