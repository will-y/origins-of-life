package dev.willyelton.origins.common.item;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.entity.CreatureEntity;
import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ScalpelItem extends Item {
    public ScalpelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand type) {
        if (target instanceof CreatureEntity creatureEntity) {
            EntityData entityData = creatureEntity.entityData();
            if (entityData != null) {
                ItemStack drop = new ItemStack(OriginsOfLife.DNA_SAMPLE.get());
                drop.set(DataComponents.ENTITY_DATA, entityData);

                player.level().playSound(null, target, SoundEvents.BOGGED_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);

                if (player.level() instanceof ServerLevel serverLevel) {
                    target.spawnAtLocation(serverLevel, drop);
                    itemStack.hurtAndBreak(1, player, type.asEquipmentSlot());
                    target.hurtServer(serverLevel, target.damageSources().playerAttack(player), 0.5F);
                }
            }
        }

        return super.interactLivingEntity(itemStack, player, target, type);
    }
}
