package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;

import java.util.Optional;

public class OriginOfLifeModels extends ModelProvider {
    public OriginOfLifeModels(PackOutput output) {
        super(output, OriginsOfLife.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createNonTemplateModelBlock(OriginsOfLife.PRIMORDIAL_SOUP_BLOCK.get());

        itemModels.itemModelOutput.accept(OriginsOfLife.PRIMORDIAL_SOUP_BUCKET.get(), new DynamicFluidContainerModel.Unbaked(
                new DynamicFluidContainerModel.Textures(
                        Optional.of(new Material(Identifier.withDefaultNamespace("item/bucket"))),
                        Optional.of(new Material(Identifier.withDefaultNamespace("item/bucket"))),
                        Optional.of(new Material(Identifier.fromNamespaceAndPath("neoforge", "item/mask/bucket_fluid"))),
                        Optional.empty()
                ),
                OriginsOfLife.PRIMORDIAL_SOUP.get(),
                false,
                true,
                true));
        itemModels.generateFlatItem(OriginsOfLife.AQUATIC_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
    }
}
