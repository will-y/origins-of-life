package dev.willyelton.origins.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class OriginsOfLifeTags {
    // Item
    public static final TagKey<Item> FOSSIL_SPEED_ITEMS = ItemTags.create(rl("fossil_speed_items"));
    public static final TagKey<Item> FOSSIL_PLAYER_BEHAVIOR_AGGRESSIVE = ItemTags.create(rl("fossil_player_behavior_aggressive"));
    public static final TagKey<Item> FOSSIL_PLAYER_BEHAVIOR_AFRAID = ItemTags.create(rl("fossil_player_behavior_afraid"));
    public static final TagKey<Item> FOSSIL_PLAYER_BEHAVIOR_NEUTRAL = ItemTags.create(rl("fossil_player_behavior_neutral"));
    public static final TagKey<Item> FOSSIL_MOB_BEHAVIOR_AGGRESSIVE = ItemTags.create(rl("fossil_mob_behavior_aggressive"));
    public static final TagKey<Item> FOSSIL_MOB_BEHAVIOR_AFRAID = ItemTags.create(rl("fossil_mob_behavior_afraid"));
    public static final TagKey<Item> FOSSIL_MOB_BEHAVIOR_NEUTRAL = ItemTags.create(rl("fossil_mob_behavior_neutral"));

    // Fluid
    public static final TagKey<Fluid> PRIMORDIAL_SOUP = FluidTags.create(rl("primordial_soup"));

    // Entity Type
    public static final TagKey<EntityType<?>> PICKUP_ENTITY_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, rl("pickup_entity_blacklist"));

}
