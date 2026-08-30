package dev.willyelton.origins.common.entity;

import dev.willyelton.origins.common.entity.data.EntityData;
import dev.willyelton.origins.common.entity.data.EntityDataGenerator;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.Map;

public abstract class CreatureEntity extends Animal implements IEntityWithComplexSpawn {

//    private static final EntityDataAccessor<EntityData> ENTITY_DATA_STATE = SynchedEntityData.defineId(
//            CreatureEntity.class, EntityDataSerializer.forValueType(EntityData.STREAM_CODEC));

    private EntityData entityData;

    protected CreatureEntity(EntityType<? extends Animal> type, Level level, EntityData entityData) {
        this.entityData = entityData;
        super(type, level);
        init();
    }

    private void init() {
        this.dimensions = createDimensions(entityData);
        AttributeSupplier.Builder attributeBuilder = Mob.createMobAttributes();

        for (Map.Entry<Holder<Attribute>, Double> entry : entityData.defaultAttributes().entrySet()) {
            attributeBuilder.add(entry.getKey(), entry.getValue());
        }

        this.attributes = new AttributeMap(attributeBuilder.build());
        refreshDimensions();

        // Redo some things in the living entity
        this.setHealth(this.getMaxHealth());
    }

    protected CreatureEntity(EntityType<? extends Animal> type, Level level) {
        this(type, level, EntityDataGenerator.random(level));
    }

    public EntityData entityData() {
        return entityData;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
//        entityData.define(ENTITY_DATA_STATE, EntityDataGenerator.empty());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData = input.read("entity_data", EntityData.CODEC).orElse(EntityDataGenerator.empty());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("entity_data", EntityData.CODEC,  this.entityData);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        EntityData.STREAM_CODEC.encode(buffer, this.entityData);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        this.entityData = EntityData.STREAM_CODEC.decode(additionalData);
        init();
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return dimensions.scale(this.getAgeScale());
    }

    private EntityDimensions createDimensions(EntityData entityData) {
        EntityData.Sizes sizes = entityData.sizes();
        float x = Math.min(3, (sizes.maxX() / 16.0F) * this.getScale());
        float y = (sizes.maxY() / 16.0F) * this.getScale() * 0.8F;

        return EntityDimensions.scalable(x, y).withEyeHeight(y * 0.85F);
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        return this.dimensions.makeBoundingBox(position);
//        EntityData.Sizes sizes = entityData.sizes();
//        return new AABB(position.x - sizes.maxX() / 32.0, position.y, position.z - sizes.maxX() / 32.0,
//                position.x + sizes.maxX() / 32.0, position.y + sizes.maxY() / 16.0, position.z + sizes.maxX() / 32.0);
        // Closer but can't rotate these:
//        return new AABB(position.x - sizes.maxX() / 32.0, position.y, position.z - sizes.maxZ() / 32.0,
//                position.x + sizes.maxX() / 32.0, position.y + sizes.maxY() / 16.0, position.z + sizes.maxZ() / 32.0);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(entityData.foodTag());
    }
}
