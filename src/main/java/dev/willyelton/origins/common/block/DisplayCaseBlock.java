package dev.willyelton.origins.common.block;

import com.mojang.serialization.MapCodec;
import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.block.entity.DisplayCaseBlockEntity;
import dev.willyelton.origins.common.entity.data.EntityData;
import dev.willyelton.origins.common.item.CageItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DisplayCaseBlock extends BaseEntityBlock {
    public static final MapCodec<DisplayCaseBlock> CODEC = simpleCodec(DisplayCaseBlock::new);

    public DisplayCaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new DisplayCaseBlockEntity(worldPosition, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            if (stack.is(OriginsOfLife.CAGE)) {
                DisplayCaseBlockEntity blockEntity = (DisplayCaseBlockEntity) level.getBlockEntity(pos);
                if (blockEntity != null) {
                    EntityData cageEntityData = stack.get(DataComponents.ENTITY_DATA);
                    if (blockEntity.entityData() == null) {
                        if (cageEntityData != null) {
                            blockEntity.setEntityData(cageEntityData, player);
                            CageItem.removeDataComponents(stack);

                            return InteractionResult.SUCCESS_SERVER;
                        }
                    } else if (cageEntityData == null) {
                        ((CageItem) stack.getItem()).captureMob(stack, serverLevel, serverPlayer, (LivingEntity) blockEntity.displayEntity());
                        blockEntity.setEntityData(null, player);
                        blockEntity.setChanged();
                    }
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
