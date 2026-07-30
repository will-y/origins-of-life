package dev.willyelton.origins;

import com.mojang.logging.LogUtils;
import dev.willyelton.origins.common.entity.AquaticCreature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(OriginsOfLife.MODID)
public class OriginsOfLife {
    public static final String MODID = "originsoflife";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OriginsOfLife.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, OriginsOfLife.MODID);

    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<AquaticCreature>> AQUATIC_CREATURE = ENTITIES.register("aquatic_creature",
            () -> EntityType.Builder.of(AquaticCreature::new, MobCategory.WATER_CREATURE)
                    .sized(1, 1)
                    .eyeHeight(0.8F)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, rl("aquatic_creature"))));

    // Items
    public static final DeferredItem<SpawnEggItem> AQUATIC_SPAWN_EGG = ITEMS.registerItem("aquatic_spawn_egg", properties ->
            new SpawnEggItem(properties.spawnEgg(AQUATIC_CREATURE.get())));

    public OriginsOfLife(IEventBus modEventBus, ModContainer modContainer) {
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
