package dev.willyelton.origins.datagen;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.client.model.property.HasEntityDataProperty;
import dev.willyelton.origins.client.model.property.HasVanillaEntityDataProperty;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;

import java.util.Optional;

public class OriginsOfLifeModels extends ModelProvider {
    public OriginsOfLifeModels(PackOutput output) {
        super(output, OriginsOfLife.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createNonTemplateModelBlock(OriginsOfLife.PRIMORDIAL_SOUP_BLOCK.get());
        blockModels.createTrivialCube(OriginsOfLife.FOSSIL_BLOCK_DEEPSLATE.get());
        blockModels.createTrivialCube(OriginsOfLife.FOSSIL_BLOCK_SULFUR.get());
        blockModels.createTrivialCube(OriginsOfLife.FOSSIL_BLOCK_CLAY.get());
        blockModels.createTrivialCube(OriginsOfLife.DISPLAY_CASE.get());

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
        itemModels.generateFlatItem(OriginsOfLife.RAW_MEAT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(OriginsOfLife.COOKED_MEAT.get(), ModelTemplates.FLAT_ITEM);

        generateFossil(itemModels);
        generateCage(itemModels);
    }

    private void generateFossil(ItemModelGenerators itemModels) {
        Item fossilItem = OriginsOfLife.FOSSIL.get();
        ItemModel.Unbaked dirty = ItemModelUtils.plainModel(itemModels.createFlatItemModel(fossilItem, "_dirty", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked clean = ItemModelUtils.plainModel(itemModels.createFlatItemModel(fossilItem, "_clean", ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(fossilItem, new ConditionalItemModel.Unbaked(
                Optional.empty(),
                new HasEntityDataProperty(),
                clean,
                dirty));
    }

    private void generateCage(ItemModelGenerators itemModels) {
        Item fossilItem = OriginsOfLife.CAGE.get();
        ItemModel.Unbaked empty = ItemModelUtils.plainModel(itemModels.createFlatItemModel(fossilItem, ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked full = ItemModelUtils.plainModel(itemModels.createFlatItemModel(fossilItem, "_full", ModelTemplates.FLAT_HANDHELD_ITEM));

        itemModels.itemModelOutput.accept(fossilItem, new ConditionalItemModel.Unbaked(
                Optional.empty(),
                new HasVanillaEntityDataProperty(),
                full,
                empty));
    }
}
