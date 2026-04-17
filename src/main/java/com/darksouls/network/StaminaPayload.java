package com.darksouls.network;

import com.darksouls.DarkSoulsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record StaminaPayload(float current, float max) implements CustomPayload {
    public static final CustomPayload.Id<StaminaPayload> ID =
        new CustomPayload.Id<>(DarkSoulsMod.id("stamina"));

    public static final PacketCodec<PacketByteBuf, StaminaPayload> CODEC =
        PacketCodec.of(
            (payload, buf) -> { buf.writeFloat(payload.current); buf.writeFloat(payload.max); },
            buf -> new StaminaPayload(buf.readFloat(), buf.readFloat())
        );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
