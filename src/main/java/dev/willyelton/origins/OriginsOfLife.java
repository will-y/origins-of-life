package dev.willyelton.origins;

import com.mojang.logging.LogUtils;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.entity.AquaticCreature;
import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import dev.willyelton.origins.common.item.CageItem;
import dev.willyelton.origins.common.item.FossilItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

import java.util.List;

import static dev.willyelton.origins.common.event.RegisterCauldronInteractionEvent.cleanFossil;

@Mod(OriginsOfLife.MODID)
public class OriginsOfLife {
    public static final String MODID = "origins_of_life";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OriginsOfLife.MODID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OriginsOfLife.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, OriginsOfLife.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Blocks
    public static final DeferredHolder<Block, Block> FOSSIL_BLOCK_DEEPSLATE = BLOCKS.registerBlock("fossil_block_deepslate", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE));
    public static final DeferredHolder<Block, Block> FOSSIL_BLOCK_SULFUR = BLOCKS.registerBlock("fossil_block_sulfur", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE));
    public static final DeferredHolder<Block, Block> FOSSIL_BLOCK_CLAY = BLOCKS.registerBlock("fossil_block_clay", Block::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE));

    // Block Items
    public static final DeferredItem<BlockItem> FOSSIL_BLOCK_ITEM_DEEPSLATE = ITEMS.registerSimpleBlockItem(FOSSIL_BLOCK_DEEPSLATE);
    public static final DeferredItem<BlockItem> FOSSIL_BLOCK_ITEM_SULFUR = ITEMS.registerSimpleBlockItem(FOSSIL_BLOCK_SULFUR);
    public static final DeferredItem<BlockItem> FOSSIL_BLOCK_ITEM_CLAY = ITEMS.registerSimpleBlockItem(FOSSIL_BLOCK_CLAY);

    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<AquaticCreature>> AQUATIC_CREATURE = ENTITIES.register("aquatic_creature",
            () -> EntityType.Builder.of(((EntityType.EntityFactory<AquaticCreature>) AquaticCreature::new), MobCategory.WATER_CREATURE)
                    .sized(1, 10)
                    .eyeHeight(0.8F)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, rl("aquatic_creature"))));

    // Items
    public static final DeferredItem<SpawnEggItem> AQUATIC_SPAWN_EGG = ITEMS.registerItem("aquatic_spawn_egg", properties ->
            new SpawnEggItem(properties.spawnEgg(AQUATIC_CREATURE.get())));
    public static final DeferredItem<Item> FOSSIL = ITEMS.registerItem("fossil", FossilItem::new,
            properties -> properties.component(net.minecraft.core.component.DataComponents.LORE, new ItemLore(
                    List.of(Component.translatable("lore.origins_of_life.fossil_dirty")))));
    public static final DeferredItem<CageItem> CAGE = ITEMS.registerItem("cage", CageItem::new);

    // Fluids
    public static final DeferredHolder<FluidType, FluidType> PRIMORDIAL_SOUP_TYPE = FLUID_TYPES.register(
            "primordial_soup",
            id -> new FluidType(FluidType.Properties.create()
                    .descriptionId(Util.makeDescriptionId("fluid", id))
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .canDrown(true)
                    .canSwim(true)
                    .isWaterLike(true)
                    .supportsBoating(true)
                    .lightLevel(2)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PRIMORDIAL_SOUP = FLUIDS.register(
            "primordial_soup",
            () -> new BaseFlowingFluid.Source(OriginsOfLife.PRIMORDIAL_SOUP_PROPERTIES));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_PRIMORDIAL_SOUP = FLUIDS.register(
            "flowing_primordial_soup",
            () -> new BaseFlowingFluid.Flowing(OriginsOfLife.PRIMORDIAL_SOUP_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> PRIMORDIAL_SOUP_BLOCK = BLOCKS.registerBlock(
            "primordial_soup",
            properties -> new LiquidBlock(PRIMORDIAL_SOUP.get(), properties),
            () -> BlockBehaviour.Properties.of()
                    .liquid()
                    .noLootTable()
                    .noCollision()
                    .replaceable()
                    .pushReaction(PushReaction.DESTROY)
                    .sound(SoundType.EMPTY)
                    .strength(100)
                    .lightLevel(_ -> 2));

    public static final DeferredItem<BucketItem> PRIMORDIAL_SOUP_BUCKET = ITEMS.registerItem(
            "primordial_soup_bucket",
            properties -> new BucketItem(OriginsOfLife.PRIMORDIAL_SOUP.get(), properties),
            () -> new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)
    );

    public static final BaseFlowingFluid.Properties PRIMORDIAL_SOUP_PROPERTIES =
            new BaseFlowingFluid.Properties(PRIMORDIAL_SOUP_TYPE, PRIMORDIAL_SOUP, FLOWING_PRIMORDIAL_SOUP)
                    .block(OriginsOfLife.PRIMORDIAL_SOUP_BLOCK)
                    .bucket(PRIMORDIAL_SOUP_BUCKET);

    // Creative Tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("origins_of_life_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("tab.origins_of_life"))
                    .icon(() -> cleanFossil(null))
                    .displayItems((params, output) -> {
                        output.accept(AQUATIC_SPAWN_EGG);
                        output.accept(FOSSIL_BLOCK_ITEM_DEEPSLATE);
                        output.accept(FOSSIL_BLOCK_ITEM_SULFUR);
                        output.accept(FOSSIL_BLOCK_ITEM_CLAY);
                        output.accept(FOSSIL);
                        output.accept(cleanFossil(null));
                        output.accept(PRIMORDIAL_SOUP_BUCKET);
                        output.accept(CAGE);
                    })
                    .build());

    public OriginsOfLife(IEventBus modEventBus, ModContainer modContainer) {
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        TABS.register(modEventBus);
        DataComponents.COMPONENTS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        Behavior.bootstrap();
    }

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
