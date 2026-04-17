package com.darksouls.network;

import com.darksouls.DarkSoulsMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * S2C bonfire events that drive client-only UX steps:
 * - activation feedback ("BONFIRE LIT")
 * - rest transition (camera/smoke/menu timing)
 */
public record BonfireEventPayload(int eventType, int transitionTicks) implements CustomPayload {
    public static final int EVENT_ACTIVATED = 0;
    public static final int EVENT_REST_START = 1;

    private static final int DEFAULT_REST_TRANSITION_TICKS = 34;

    public static final CustomPayload.Id<BonfireEventPayload> ID =
            new CustomPayload.Id<>(DarkSoulsMod.id("bonfire_event"));

    public static final PacketCodec<PacketByteBuf, BonfireEventPayload> CODEC =
            PacketCodec.of(
                    (payload, buf) -> {
                        buf.writeVarInt(payload.eventType);
                        buf.writeVarInt(payload.transitionTicks);
                    },
                    buf -> new BonfireEventPayload(buf.readVarInt(), buf.readVarInt())
            );

    public static BonfireEventPayload activated() {
        return new BonfireEventPayload(EVENT_ACTIVATED, 0);
    }

    public static BonfireEventPayload restStart() {
        return new BonfireEventPayload(EVENT_REST_START, DEFAULT_REST_TRANSITION_TICKS);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
