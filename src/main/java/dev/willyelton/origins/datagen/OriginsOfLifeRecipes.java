package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.recipe.DnaSampleFossilRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmokingRecipe;

import java.util.concurrent.CompletableFuture;

public class OriginsOfLifeRecipes extends RecipeProvider {
    public static final Criterion<InventoryChangeTrigger.TriggerInstance> HAS_FOSSIL = InventoryChangeTrigger.TriggerInstance.hasItems(OriginsOfLife.FOSSIL.get());
    public static final Criterion<InventoryChangeTrigger.TriggerInstance> HAS_MEAT = InventoryChangeTrigger.TriggerInstance.hasItems(OriginsOfLife.RAW_MEAT.get());

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

        shaped(RecipeCategory.MISC, OriginsOfLife.DISPLAY_CASE_ITEM)
                .pattern("iii")
                .pattern("ici")
                .pattern("iii")
                .define('i', Items.GLASS)
                .define('c', OriginsOfLife.CAGE)
                .unlockedBy("has_fossil", HAS_FOSSIL)
                .save(output);

        shaped(RecipeCategory.TOOLS, OriginsOfLife.SCALPEL)
                .pattern("  i")
                .pattern(" i ")
                .pattern("i  ")
                .define('i', Items.IRON_INGOT)
                .unlockedBy("has_dna", has(OriginsOfLife.DNA_SAMPLE))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(OriginsOfLife.RAW_MEAT), RecipeCategory.FOOD, CookingBookCategory.FOOD, OriginsOfLife.COOKED_MEAT, 0.35F, 200)
                .unlockedBy("has_mystery_meat", HAS_MEAT)
                .save(this.output);
        simpleCookingRecipe("smoking", SmokingRecipe::new, 100, OriginsOfLife.RAW_MEAT, OriginsOfLife.COOKED_MEAT, 0.35F);
        simpleCookingRecipe("campfire_cooking", CampfireCookingRecipe::new, 600, OriginsOfLife.RAW_MEAT, OriginsOfLife.COOKED_MEAT, 0.35F);

        SpecialRecipeBuilder
                .special(DnaSampleFossilRecipe::new)
                .save(output, OriginsOfLife.FOSSIL.getId().toString());
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
