package dev.willyelton.origins.common.event;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.item.CageItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = OriginsOfLife.MODID)
public class PlayerInteractEntityEvent {
    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (captureEntity(event)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    private static boolean captureEntity(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof CageItem mobCaptureTool && event.getLevel() instanceof ServerLevel serverLevel
                && event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof LivingEntity livingEntity) {
            return mobCaptureTool.captureMob(stack, serverLevel, serverPlayer, livingEntity);
        }

        return false;
    }
}
