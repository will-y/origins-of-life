package dev.willyelton.origins.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class AquaticCreature extends CreatureEntity {
    public AquaticCreature(EntityType<? extends AquaticCreature> type, Level level) {
        super(type, level);
    }
}
