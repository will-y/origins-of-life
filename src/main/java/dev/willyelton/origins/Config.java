package dev.willyelton.origins;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue FOSSIL_TRANSFORM_TICKS = BUILDER
            .comment("Ticks that it takes to transform a fossil into a creature")
            .defineInRange("fossil_transform_ticks", 400, 1, 10000);

    public static final ModConfigSpec.BooleanValue CAGE_PICKUP_OTHER_ENTITIES = BUILDER
            .comment("Allows the cage item to pick up all mobs")
            .define("cage_pickup_other_entities", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
