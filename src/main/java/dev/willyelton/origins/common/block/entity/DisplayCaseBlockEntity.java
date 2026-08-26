package dev.willyelton.origins.common.block.entity;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

public class DisplayCaseBlockEntity extends BlockEntity {
    private @Nullable EntityData entityData;
    private @Nullable Entity entity;

    public DisplayCaseBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(OriginsOfLife.DISPLAY_CASE_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.entityData = input.read("entity_data", EntityData.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.entityData != null) {
            output.store("entity_data", EntityData.CODEC, this.entityData);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public void saveCustomOnly(ValueOutput output) {
        super.saveCustomOnly(output);
        this.saveAdditional(output);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        Entity entity = this.displayEntity();
        if (level != null && entity != null) {
            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
            level.addFreshEntity(entity);
        }
    }

    public EntityData entityData() {
        return this.entityData;
    }

    public void setEntityData(@Nullable EntityData entityData) {
        this.entityData = entityData;
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_NEIGHBORS | UPDATE_CLIENTS);
        }
        this.setChanged();
    }

    public @Nullable Entity displayEntity() {
        if (this.entityData == null || this.level == null) {
            return null;
        }

        if (entity == null) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(OriginsOfLife.AQUATIC_CREATURE.get()).toString());
            tag.put("entity_data", EntityData.CODEC.encodeStart(NbtOps.INSTANCE, entityData).getOrThrow());

            entity = EntityType.loadEntityRecursive(tag, level, new EntitySpawnRequest(EntitySpawnReason.TRIAL_SPAWNER, true), BaseSpawner.SET_DISPLAY_ENTITY_ID);
            if (entity instanceof LivingEntity livingEntity) {
                AttributeInstance attribute = livingEntity.getAttributes().getInstance(Attributes.SCALE);
                if (attribute != null) {
                    attribute.removeModifiers();
                    attribute.setBaseValue(1.0);
                }
            }
//            entity = new AquaticCreature(this.level, this.entityData);
        }

        return entity;
    }
}
