package com.darksouls.network;

import com.darksouls.DarkSoulsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** C2S: client requests character creation with the given name, class and gift. */
public record CreateCharacterPayload(String name, int classOrd, int giftOrd) implements CustomPayload {
    public static final CustomPayload.Id<CreateCharacterPayload> ID =
            new CustomPayload.Id<>(DarkSoulsMod.id("create_character"));

    public static final PacketCodec<PacketByteBuf, CreateCharacterPayload> CODEC =
            PacketCodec.of(
                    (p, buf) -> {
                        buf.writeString(p.name == null ? "" : p.name);
                        buf.writeVarInt(p.classOrd);
                        buf.writeVarInt(p.giftOrd);
                    },
                    buf -> new CreateCharacterPayload(buf.readString(64), buf.readVarInt(), buf.readVarInt())
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
