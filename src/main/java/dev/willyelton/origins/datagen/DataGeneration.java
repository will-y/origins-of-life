package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = OriginsOfLife.MODID)
public class DataGeneration {
    @SubscribeEvent
    public static void generate(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new OriginsOfLifeModels(packOutput));

        OriginsOfLifeBlockTagsGen blockTags = new OriginsOfLifeBlockTagsGen(packOutput, lookupProvider);
        generator.addProvider(true, blockTags);

        OriginsOfLifeItemTagsGen itemTags = new OriginsOfLifeItemTagsGen(packOutput, lookupProvider);
        generator.addProvider(true, itemTags);

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(OriginsOfLifeLootTables::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(OriginsOfLifeEntityLoot::new, LootContextParamSets.ENTITY)), lookupProvider));

        generator.addProvider(true, new OriginsOfLifeFluidTagsGen(packOutput, lookupProvider));

        generator.addProvider(true, new OriginsOfLifeRecipes.Runner(packOutput, lookupProvider));
    }
}
