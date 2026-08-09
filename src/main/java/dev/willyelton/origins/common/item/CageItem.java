package dev.willyelton.origins.common.item;

import dev.willyelton.origins.Config;
import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.entity.CreatureEntity;
import dev.willyelton.origins.common.tag.OriginsOfLifeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CageItem extends Item {
    public CageItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos relativeBlockPos = blockPos.relative(direction);

            if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(relativeBlockPos, direction, stack)) {
                return InteractionResult.FAIL;
            } else if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                this.releaseMob(stack, serverLevel, relativeBlockPos, serverPlayer);
            }
        }

        return InteractionResult.PASS;
    }

    /// Returns a component with the failure reason if cannot capture
    public Component canCapture(ItemStack stack, ServerPlayer player, LivingEntity livingEntity) {
        if (stack.has(DataComponents.ENTITY_DATA)) {
            return Component.translatable("tooltip.origins_of_life.cage_full");
        }

        if (livingEntity.is(OriginsOfLifeTags.PICKUP_ENTITY_BLACKLIST)) {
            return Component.translatable("tooltip.origins_of_life.cage_blacklist");
        }

        if (livingEntity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() != player) {
            return Component.translatable("tooltip.origins_of_life.cage_not_owned");
        }

        if (Config.CAGE_PICKUP_OTHER_ENTITIES.get() || livingEntity instanceof CreatureEntity) {
            return null;
        } else {
            return Component.translatable("tooltip.origins_of_life.cage_blacklist");
        }
    }

    public boolean captureMob(ItemStack stack, ServerLevel level, @Nullable ServerPlayer player, LivingEntity livingEntity) {
        Component cannotCapture = canCapture(stack, player, livingEntity);
        if (cannotCapture == null) {
            TagValueOutput valueOutput = TagValueOutput.createWithContext(reporter(player, livingEntity), level.registryAccess());
            livingEntity.saveWithoutId(valueOutput);

            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(livingEntity.getType(), valueOutput.buildResult()));
            stack.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("tooltip.origins_of_life.cage_contains", livingEntity.getDisplayName()))));
            stack.set(dev.willyelton.origins.common.DataComponents.INSERT_TIME, level.getGameTime());
            livingEntity.discard();

            return true;
        } else {
            if (player != null) {
                player.sendOverlayMessage(cannotCapture);
            }
        }

        return false;
    }

    public void releaseMob(ItemStack stack, ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (level.getGameTime() <= stack.getOrDefault(dev.willyelton.origins.common.DataComponents.INSERT_TIME, 0L) + 3) {
            return;
        }

        TypedEntityData<EntityType<?>> entityData = stack.get(DataComponents.ENTITY_DATA);

        if (entityData != null) {
            Entity entity = entityData.type().create(level, EntityType.createDefaultStackConfig(level, stack, null), pos, EntitySpawnReason.MOB_SUMMONED, true, false);
            ValueInput valueInput = TagValueInput.create(reporter(player, entity), level.registryAccess(), entityData.copyTagWithoutId());

            if (entity != null) {
                entity.load(valueInput);
                entity.setPos(Vec3.atCenterOf(pos));
                level.addFreshEntity(entity);

                if (entity instanceof Mob mob) {
                    mob.playAmbientSound();
                }

                stack.remove(DataComponents.ENTITY_DATA);
                stack.remove(DataComponents.LORE);
                stack.remove(dev.willyelton.origins.common.DataComponents.INSERT_TIME);
            }
        }
    }

    private ProblemReporter reporter(@Nullable ServerPlayer player, Entity entity) {
        if (player == null) {
            return new ProblemReporter.ScopedCollector(entity.problemPath(), OriginsOfLife.LOGGER);
        }

        return new ProblemReporter.ScopedCollector(player.problemPath(), OriginsOfLife.LOGGER);
    }
}
