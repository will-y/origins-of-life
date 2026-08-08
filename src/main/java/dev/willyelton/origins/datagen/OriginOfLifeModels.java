package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.client.model.property.FossilProperty;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;

import java.util.Optional;

public class OriginOfLifeModels extends ModelProvider {
    public OriginOfLifeModels(PackOutput output) {
        super(output, OriginsOfLife.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createNonTemplateModelBlock(OriginsOfLife.PRIMORDIAL_SOUP_BLOCK.get());
        blockModels.createTrivialCube(OriginsOfLife.FOSSIL_BLOCK.get());

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

        generateFossil(itemModels);
    }

    private void generateFossil(ItemModelGenerators itemModels) {
        Item fossilItem = OriginsOfLife.FOSSIL.get();
        ItemModel.Unbaked dirty = ItemModelUtils.plainModel(itemModels.createFlatItemModel(fossilItem, "_dirty", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked clean = ItemModelUtils.plainModel(itemModels.createFlatItemModel(fossilItem, "_clean", ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(fossilItem, new ConditionalItemModel.Unbaked(
                Optional.empty(),
                new FossilProperty(),
                clean,
                dirty));
    }
}
