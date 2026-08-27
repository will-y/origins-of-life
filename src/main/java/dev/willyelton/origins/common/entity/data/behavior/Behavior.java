package dev.willyelton.origins.common.entity.data.behavior;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.willyelton.origins.common.entity.CreatureEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

import java.util.List;

import static dev.willyelton.origins.OriginsOfLife.rl;

public interface Behavior {

    ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends Behavior>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    BiMap<Identifier, StreamCodec<FriendlyByteBuf, ? extends Behavior>> STREAM_CODEC_BI_MAP = HashBiMap.create();
    StreamCodec<FriendlyByteBuf, Identifier> IDENTIFIER_STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeIdentifier, FriendlyByteBuf::readIdentifier);

    Codec<Behavior> CODEC = ID_MAPPER.codec(Identifier.CODEC).dispatch(Behavior::codec, c -> c);
    StreamCodec<FriendlyByteBuf, Behavior> STREAM_CODEC = IDENTIFIER_STREAM_CODEC.dispatch(Behavior::identifier, STREAM_CODEC_BI_MAP::get);

    static void bootstrap() {
        ID_MAPPER.put(rl("player_behavior"), PlayerBehavior.CODEC);

        STREAM_CODEC_BI_MAP.put(rl("player_behavior"), PlayerBehavior.STREAM_CODEC);
    }

    String behaviorName();

    Identifier identifier();

    String description();

    List<Goal> createGoals(CreatureEntity entity);

    MapCodec<? extends Behavior> codec();

    default List<TargetGoal> createTargetGoals(CreatureEntity entity) {
        return List.of();
    }

    default int displayColor() {
        return -1;
    }
}
