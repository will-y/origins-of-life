package dev.willyelton.origins.client.model.property;

import com.mojang.serialization.MapCodec;
import dev.willyelton.origins.common.DataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record FossilProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<FossilProperty> MAP_CODEC = MapCodec.unit(FossilProperty::new);

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return itemStack.has(DataComponents.ENTITY_DATA);
    }
}
