package dev.willyelton.origins.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class CreatureEntity extends PathfinderMob {
    private final EntityData entityData = EntityDataGenerator.random();

    protected CreatureEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public EntityData entityData() {
        return entityData;
    }
}
