package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class OriginsOfLifeItemTagsGen extends ItemTagsProvider {
    public OriginsOfLifeItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, OriginsOfLife.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_BOUNCY).add(
                OriginsOfLife.FOSSIL_BLOCK_ITEM_DEEPSLATE.getKey(),
                OriginsOfLife.FOSSIL_BLOCK_ITEM_SULFUR.getKey());

        tag(ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR).add(
                OriginsOfLife.FOSSIL_BLOCK_ITEM_CLAY.getKey());
    }
}
