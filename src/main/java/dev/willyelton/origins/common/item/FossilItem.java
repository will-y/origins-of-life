package dev.willyelton.origins.common.item;

import dev.willyelton.origins.Config;
import dev.willyelton.origins.common.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class FossilItem extends Item {
    public FossilItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (stack.has(DataComponents.ENTITY_DATA)) {
            int progress = stack.getOrDefault(DataComponents.FOSSIL_TRANSFORM_COUNTER, 0);

            if (progress > 0) {
                builder.accept(Component.translatable("tooltip.origins_of_life.fossil", (progress / (float) Config.FOSSIL_TRANSFORM_TICKS.get()) * 100));
            }
        }

    }
}
