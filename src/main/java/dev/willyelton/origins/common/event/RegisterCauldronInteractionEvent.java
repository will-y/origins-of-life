package dev.willyelton.origins.common.event;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.entity.EntityDataGenerator;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = OriginsOfLife.MODID)
public class RegisterCauldronInteractionEvent {
    @SubscribeEvent
    public static void registerCauldronInteraction(net.neoforged.neoforge.event.RegisterCauldronInteractionEvent.Interaction event) {
        event.register(Identifier.parse("water"), OriginsOfLife.FOSSIL.get(), (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide() && !stack.has(DataComponents.ENTITY_DATA)) {
                Item usedItem = stack.getItem();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, cleanFossil()));
                player.awardStat(Stats.USE_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(usedItem));
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.SUCCESS;
        });
    }

    public static ItemStack cleanFossil() {
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(DataComponents.ENTITY_DATA.get(), EntityDataGenerator.random())
                .set(net.minecraft.core.component.DataComponents.ITEM_NAME, Component.translatable("item.origins_of_life.fossil_clean"))
                .set(net.minecraft.core.component.DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.origins_of_life.fossil_clean"))))
                .build();

        return new ItemStack(OriginsOfLife.FOSSIL, 1, patch);
    }
}
