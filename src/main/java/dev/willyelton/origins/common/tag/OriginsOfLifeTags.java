package dev.willyelton.origins.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluid;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class OriginsOfLifeTags {
    // Fluid
    public static final TagKey<Fluid> PRIMORDIAL_SOUP = FluidTags.create(rl("primordial_soup"));

    // Entity Type
    public static final TagKey<EntityType<?>> PICKUP_ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, rl("pickup_entity_blacklist"));

}
