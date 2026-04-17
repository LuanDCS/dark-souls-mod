package com.darksouls.network;

import com.darksouls.DarkSoulsMod;
import com.darksouls.stats.CharacterData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/**
 * Full character state pushed from server to client. Covers every field the
 * screen and HUD need; sent on join, respawn, and after creation.
 */
public record CharacterSyncPayload(
        boolean created,
        String name,
        int classOrd,
        int giftOrd,
        int level,
        int vitality,
        int attunement,
        int endurance,
        int strength,
        int dexterity,
        int resistance,
        int intelligence,
        int faith,
        int humanity
) implements CustomPayload {
    public static final CustomPayload.Id<CharacterSyncPayload> ID =
            new CustomPayload.Id<>(DarkSoulsMod.id("character_sync"));

    public static final PacketCodec<PacketByteBuf, CharacterSyncPayload> CODEC =
            PacketCodec.of(CharacterSyncPayload::write, CharacterSyncPayload::read);

    public static CharacterSyncPayload from(CharacterData d) {
        return new CharacterSyncPayload(
                d.created, d.name, d.characterClass.ordinal(), d.gift.ordinal(), d.level,
                d.vitality, d.attunement, d.endurance, d.strength, d.dexterity,
                d.resistance, d.intelligence, d.faith, d.humanity);
    }

    private static void write(CharacterSyncPayload p, PacketByteBuf buf) {
        buf.writeBoolean(p.created);
        buf.writeString(p.name == null ? "" : p.name);
        buf.writeVarInt(p.classOrd);
        buf.writeVarInt(p.giftOrd);
        buf.writeVarInt(p.level);
        buf.writeVarInt(p.vitality);
        buf.writeVarInt(p.attunement);
        buf.writeVarInt(p.endurance);
        buf.writeVarInt(p.strength);
        buf.writeVarInt(p.dexterity);
        buf.writeVarInt(p.resistance);
        buf.writeVarInt(p.intelligence);
        buf.writeVarInt(p.faith);
        buf.writeVarInt(p.humanity);
    }

    private static CharacterSyncPayload read(PacketByteBuf buf) {
        return new CharacterSyncPayload(
                buf.readBoolean(),
                buf.readString(64),
                buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(), buf.readVarInt(),
                buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
