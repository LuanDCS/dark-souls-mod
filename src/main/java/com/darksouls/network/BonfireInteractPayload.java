package com.darksouls.network;

import com.darksouls.DarkSoulsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

/** C2S request to interact with a specific nearby bonfire block. */
public record BonfireInteractPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<BonfireInteractPayload> ID =
            new CustomPayload.Id<>(DarkSoulsMod.id("bonfire_interact"));

    public static final PacketCodec<PacketByteBuf, BonfireInteractPayload> CODEC =
            PacketCodec.of(
                    (payload, buf) -> buf.writeBlockPos(payload.pos),
                    buf -> new BonfireInteractPayload(buf.readBlockPos())
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
