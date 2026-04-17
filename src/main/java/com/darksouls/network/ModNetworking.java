package com.darksouls.network;

import com.darksouls.bonfire.BonfireManager;
import com.darksouls.client.BonfireHandler;
import com.darksouls.stats.CharacterClass;
import com.darksouls.stats.CharacterManager;
import com.darksouls.stats.ClientStats;
import com.darksouls.stats.Gift;
import com.darksouls.stats.StaminaManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public final class ModNetworking {
    private ModNetworking() {}

    public static void registerServer() {
        PayloadTypeRegistry.playS2C().register(StaminaPayload.ID, StaminaPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CharacterSyncPayload.ID, CharacterSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BonfireEventPayload.ID, BonfireEventPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CreateCharacterPayload.ID, CreateCharacterPayload.CODEC);
<<<<<<< HEAD
        PayloadTypeRegistry.playC2S().register(RestAtBonfirePayload.ID, RestAtBonfirePayload.CODEC);
=======
        PayloadTypeRegistry.playC2S().register(BonfireInteractPayload.ID, BonfireInteractPayload.CODEC);
>>>>>>> ba47194 (Melhora no sistema de BonFire)

        ServerPlayNetworking.registerGlobalReceiver(CreateCharacterPayload.ID, (payload, ctx) -> {
            ServerPlayerEntity player = ctx.player();
            ctx.server().execute(() -> {
                CharacterClass cls = CharacterClass.fromOrdinal(payload.classOrd());
                Gift gift = Gift.fromOrdinal(payload.giftOrd());
                boolean created = CharacterManager.tryCreate(player, payload.name(), cls, gift);
                if (!created) {
                    CharacterManager.syncToClient(player);
                }
            });
        });

<<<<<<< HEAD
        ServerPlayNetworking.registerGlobalReceiver(RestAtBonfirePayload.ID, (payload, ctx) -> {
            ServerPlayerEntity player = ctx.player();
            ctx.server().execute(() -> {
                player.setHealth(player.getMaxHealth());
                player.getHungerManager().setFoodLevel(20);
=======
        ServerPlayNetworking.registerGlobalReceiver(BonfireInteractPayload.ID, (payload, ctx) -> {
            ServerPlayerEntity player = ctx.player();
            ctx.server().execute(() -> {
                BonfireManager.handleInteract(player, payload.pos());
>>>>>>> ba47194 (Melhora no sistema de BonFire)
            });
        });
    }

    public static void sendStamina(ServerPlayerEntity player, StaminaManager.StaminaState s) {
        ServerPlayNetworking.send(player, new StaminaPayload(s.current, s.max));
    }

    public static void sendBonfireEvent(ServerPlayerEntity player, BonfireEventPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientStats.reset());

        ClientPlayNetworking.registerGlobalReceiver(StaminaPayload.ID, (payload, ctx) -> {
            ClientStats.stamina = payload.current();
            ClientStats.staminaMax = payload.max();
        });
        ClientPlayNetworking.registerGlobalReceiver(CharacterSyncPayload.ID, (payload, ctx) -> {
            ClientStats.applySync(payload);
        });
        ClientPlayNetworking.registerGlobalReceiver(BonfireEventPayload.ID, (payload, ctx) -> {
            BonfireHandler.handleServerEvent(payload);
        });
    }

    @Environment(EnvType.CLIENT)
    public static void sendCreate(String name, CharacterClass cls, Gift gift) {
        ClientPlayNetworking.send(new CreateCharacterPayload(name, cls.ordinal(), gift.ordinal()));
    }

    @Environment(EnvType.CLIENT)
<<<<<<< HEAD
    public static void sendRestAtBonfire() {
        ClientPlayNetworking.send(new RestAtBonfirePayload());
=======
    public static void sendBonfireInteract(BlockPos pos) {
        ClientPlayNetworking.send(new BonfireInteractPayload(pos));
>>>>>>> ba47194 (Melhora no sistema de BonFire)
    }
}
