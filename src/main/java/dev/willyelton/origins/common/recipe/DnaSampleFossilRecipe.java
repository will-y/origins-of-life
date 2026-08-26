package dev.willyelton.origins.common.recipe;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.entity.data.EntityData;
import dev.willyelton.origins.common.entity.data.EntityDataGenerator;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DnaSampleFossilRecipe extends CustomRecipe {
    public static final DnaSampleFossilRecipe INSTANCE = new DnaSampleFossilRecipe();

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.size() != 2) {
            return false;
        }

        boolean foundDna = false;
        boolean foundRock = false;

        for (ItemStack stack : input.items()) {
            if (!foundDna && stack.is(OriginsOfLife.DNA_SAMPLE.get())) {
                foundDna = true;
                continue;
            }

            if (!foundRock && (stack.is(ItemTags.STONE_CRAFTING_MATERIALS))) {
                foundRock = true;
            }
        }

        return foundDna && foundRock;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        EntityData entityData = null;

        for (ItemStack stack : input.items()) {
            if (stack.is(OriginsOfLife.DNA_SAMPLE.get())) {
                entityData = stack.get(DataComponents.ENTITY_DATA);
            }
        }

        if (entityData == null) {
            entityData = EntityDataGenerator.random(null);
        }

        ItemStack result = new ItemStack(OriginsOfLife.FOSSIL.get());
        result.set(DataComponents.ENTITY_DATA, entityData);

        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return OriginsOfLife.DNA_FOSSIL_RECIPE.get();
    }
}
