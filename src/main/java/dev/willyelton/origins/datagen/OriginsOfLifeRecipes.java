package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class OriginsOfLifeRecipes extends RecipeProvider {
    public static final Criterion<InventoryChangeTrigger.TriggerInstance> HAS_FOSSIL = InventoryChangeTrigger.TriggerInstance.hasItems(OriginsOfLife.FOSSIL.get());

    protected OriginsOfLifeRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, OriginsOfLife.CAGE)
                .pattern("iii")
                .pattern("i i")
                .pattern("iii")
                .define('i', Items.IRON_BARS)
                .unlockedBy("has_fossil", HAS_FOSSIL)
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new OriginsOfLifeRecipes(provider, output);
        }

        @Override
        public String getName() {
            return OriginsOfLife.MODID + ":recipes";
        }
    }
}
