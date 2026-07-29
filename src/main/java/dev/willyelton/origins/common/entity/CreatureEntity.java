package dev.willyelton.origins.common.entity;

import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class CreatureEntity extends PathfinderMob {
    private final EntityData entityData;
    private final EntityDimensions entityDimensions;

    protected CreatureEntity(EntityType<? extends PathfinderMob> type, Level level) {
        this.entityData = EntityDataGenerator.random();
        super(type, level);
        entityDimensions = createDimensions(entityData);

    }

    public EntityData entityData() {
        return entityData;
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return entityDimensions;
    }

    private EntityDimensions createDimensions(EntityData entityData) {
        float x = entityData.bodySegments().stream().mapToInt(EntityData.CubeSegment::x).sum() / 16.0F;
        float y = entityData.bodySegments().stream().mapToInt(EntityData.CubeSegment::y).max().orElse(0) / 16.0F;

        return new EntityDimensions(x, y, 0.85F * y, EntityAttachments.createDefault(x, y), false);
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        EntityData.Sizes sizes = entityData.sizes();
        return new AABB(position.x - sizes.maxX() / 32.0, position.y, position.z - sizes.maxZ() / 32.0,
                position.x + sizes.maxX() / 32.0, position.y + sizes.maxY() / 16.0, position.z + sizes.maxZ() / 32.0);
    }
}
