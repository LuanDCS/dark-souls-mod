package com.darksouls;

import com.darksouls.client.BonfireHandler;
import com.darksouls.client.ModKeybindings;
import com.darksouls.hud.DarkSoulsHud;
import com.darksouls.network.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class DarkSoulsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModNetworking.registerClient();
        ModKeybindings.register();
        BonfireHandler.register();
        HudRenderCallback.EVENT.register(new DarkSoulsHud());
    }
}
