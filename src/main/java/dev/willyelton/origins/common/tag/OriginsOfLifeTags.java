package dev.willyelton.origins.common.tag;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class OriginsOfLifeTags {
    public static final TagKey<Fluid> PRIMORDIAL_SOUP = FluidTags.create(rl("primordial_soup"));
}
