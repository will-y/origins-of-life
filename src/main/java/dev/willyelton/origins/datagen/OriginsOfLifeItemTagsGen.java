package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.tag.OriginsOfLifeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
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

        tag(OriginsOfLifeTags.FOSSIL_SPEED_ITEMS).add(
                Items.SUGAR.builtInRegistryHolder().getKey(),
                Items.SUGAR_CANE.builtInRegistryHolder().getKey(),
                Items.REDSTONE.builtInRegistryHolder().getKey(),
                Items.REDSTONE_BLOCK.builtInRegistryHolder().getKey());

        tag(OriginsOfLifeTags.FOSSIL_PLAYER_BEHAVIOR_AGGRESSIVE)
                .addTag(ItemTags.SWORDS);

        tag(OriginsOfLifeTags.FOSSIL_PLAYER_BEHAVIOR_NEUTRAL)
                .addTag(ItemTags.BEE_FOOD)
                .addTag(ItemTags.LEAVES);

        tag(OriginsOfLifeTags.FOSSIL_PLAYER_BEHAVIOR_AFRAID)
                .add(Items.AMETHYST_BLOCK.builtInRegistryHolder().getKey(),
                        Items.AMETHYST_CLUSTER.builtInRegistryHolder().getKey(),
                        Items.AMETHYST_SHARD.builtInRegistryHolder().getKey(),
                        Items.BUDDING_AMETHYST.builtInRegistryHolder().getKey(),
                        Items.SMALL_AMETHYST_BUD.builtInRegistryHolder().getKey(),
                        Items.MEDIUM_AMETHYST_BUD.builtInRegistryHolder().getKey(),
                        Items.LARGE_AMETHYST_BUD.builtInRegistryHolder().getKey(),
                        Items.ENDER_PEARL.builtInRegistryHolder().getKey(),
                        Items.ENDER_EYE.builtInRegistryHolder().getKey(),
                        Items.CHORUS_FRUIT.builtInRegistryHolder().getKey(),
                        Items.SLIME_BALL.builtInRegistryHolder().getKey());
    }
}
