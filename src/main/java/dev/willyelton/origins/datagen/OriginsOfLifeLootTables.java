package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.stream.Collectors;

public class OriginsOfLifeLootTables extends VanillaBlockLoot {
    public OriginsOfLifeLootTables(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected void generate() {
        add(OriginsOfLife.FOSSIL_BLOCK_DEEPSLATE.get(), createOreDrop(OriginsOfLife.FOSSIL_BLOCK_DEEPSLATE.get(), OriginsOfLife.FOSSIL.get()));
        add(OriginsOfLife.FOSSIL_BLOCK_SULFUR.get(), createOreDrop(OriginsOfLife.FOSSIL_BLOCK_SULFUR.get(), OriginsOfLife.FOSSIL.get()));
        add(OriginsOfLife.FOSSIL_BLOCK_CLAY.get(), createOreDrop(OriginsOfLife.FOSSIL_BLOCK_CLAY.get(), OriginsOfLife.FOSSIL.get()));
        dropSelf(OriginsOfLife.DISPLAY_CASE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(OriginsOfLife.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
