package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class OriginsOfLifeBlockTagsGen extends BlockTagsProvider {
    public OriginsOfLifeBlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, OriginsOfLife.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                OriginsOfLife.FOSSIL_BLOCK_DEEPSLATE.getKey(),
                OriginsOfLife.FOSSIL_BLOCK_SULFUR.getKey());

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                OriginsOfLife.FOSSIL_BLOCK_CLAY.getKey());
    }
}
