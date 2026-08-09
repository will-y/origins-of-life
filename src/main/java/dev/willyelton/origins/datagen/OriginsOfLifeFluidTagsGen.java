package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.tag.OriginsOfLifeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.material.Fluid;

import java.util.concurrent.CompletableFuture;

public class OriginsOfLifeFluidTagsGen extends TagsProvider<Fluid> {
    protected OriginsOfLifeFluidTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.FLUID, lookupProvider, OriginsOfLife.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        getOrCreateRawBuilder(OriginsOfLifeTags.PRIMORDIAL_SOUP)
                .addElement(OriginsOfLife.PRIMORDIAL_SOUP.getId())
                .addElement(OriginsOfLife.FLOWING_PRIMORDIAL_SOUP.getId());
    }
}
