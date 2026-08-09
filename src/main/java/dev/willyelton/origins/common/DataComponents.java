package dev.willyelton.origins.common;

import com.mojang.serialization.Codec;
import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.entity.EntityData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, OriginsOfLife.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EntityData>> ENTITY_DATA = register("entity_data", EntityData.CODEC, EntityData.STREAM_CODEC);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FOSSIL_TRANSFORM_COUNTER = register("fossil_transform_counter", Codec.INT, ByteBufCodecs.INT);

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String key, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> result;
        if (streamCodec == null) {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            result = COMPONENTS.register(key, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }

        return result;
    }
}
