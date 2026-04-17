package com.darksouls.network;

import com.darksouls.DarkSoulsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RestAtBonfirePayload() implements CustomPayload {
    public static final CustomPayload.Id<RestAtBonfirePayload> ID =
        new CustomPayload.Id<>(DarkSoulsMod.id("rest_at_bonfire"));

    public static final PacketCodec<PacketByteBuf, RestAtBonfirePayload> CODEC =
        PacketCodec.of(
            (payload, buf) -> {},
            buf -> new RestAtBonfirePayload()
        );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
