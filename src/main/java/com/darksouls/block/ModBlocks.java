package com.darksouls.block;

import com.darksouls.DarkSoulsMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block BONFIRE = register("bonfire",
            new BonfireBlock(AbstractBlock.Settings.copy(Blocks.CAMPFIRE)
                    .strength(2.0f)
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()));

    public static final Item BONFIRE_ITEM = registerItem("bonfire", BONFIRE);

    private ModBlocks() {}

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(BONFIRE_ITEM));
    }

    private static Block register(String name, Block block) {
        Identifier id = DarkSoulsMod.id(name);
        return Registry.register(Registries.BLOCK, id, block);
    }

    private static Item registerItem(String name, Block block) {
        Identifier id = DarkSoulsMod.id(name);
        BlockItem item = new BlockItem(block, new Item.Settings());
        return Registry.register(Registries.ITEM, id, item);
    }
}
