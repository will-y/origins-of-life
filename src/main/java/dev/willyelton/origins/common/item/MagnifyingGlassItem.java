package dev.willyelton.origins.common.item;

import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.block.entity.DisplayCaseBlockEntity;
import dev.willyelton.origins.common.entity.CreatureEntity;
import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class MagnifyingGlassItem extends Item {
    public MagnifyingGlassItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand type) {
        if (target instanceof CreatureEntity creatureEntity) {
            InteractionResult result = use(stack, player, creatureEntity.entityData(), creatureEntity);

            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        return super.interactLivingEntity(stack, player, target, type);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player != null && blockEntity instanceof DisplayCaseBlockEntity displayCaseBlockEntity) {
            EntityData entityData = displayCaseBlockEntity.entityData();
            Entity entity = displayCaseBlockEntity.displayEntity();
            if (entityData != null && entity instanceof LivingEntity livingEntity) {
                InteractionResult result = use(stack, player, entityData, livingEntity);

                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
        }

        return super.useOn(context);
    }

    private InteractionResult use(ItemStack stack, Player player, EntityData entityData, LivingEntity entity) {
        long levelTime = player.level().getGameTime();
        long lastUsed = stack.getOrDefault(DataComponents.INSERT_TIME, 0L);
        if (levelTime - lastUsed > 5) {
            List<Component> components = entityData.displayComponents(entity);

            if (!player.level().isClientSide()) {
                for (Component component : components) {
                    player.sendSystemMessage(component);
                }
            }

            stack.set(DataComponents.INSERT_TIME, levelTime);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }
}
