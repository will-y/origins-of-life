package dev.willyelton.origins.common.entity.data.behavior;

import com.mojang.serialization.MapCodec;
import dev.willyelton.origins.common.entity.CreatureEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

import static dev.willyelton.origins.OriginsOfLife.rl;

public enum PlayerBehavior implements Behavior, StringRepresentable {
    NEUTRAL("Player Neutral", TextColor.WHITE.getValue()),
    AFRAID("Player Afraid", TextColor.AQUA.getValue()),
    AGGRESSIVE("Player Aggressive", TextColor.RED.getValue()),;

    public static final MapCodec<PlayerBehavior> CODEC = StringRepresentable.fromEnum(PlayerBehavior::values).fieldOf("playerBehavior");
    public static final StreamCodec<FriendlyByteBuf, PlayerBehavior> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(PlayerBehavior.class);

    private final String description;
    private final int displayColor;

    PlayerBehavior(String description, int displayColor) {
        this.description = description;
        this.displayColor = displayColor;
    }

    @Override
    public String behaviorName() {
        return "Player Relation";
    }

    @Override
    public Identifier identifier() {
        return rl("player_behavior");
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public int displayColor() {
        return this.displayColor;
    }

    @Override
    public List<Goal> createGoals(CreatureEntity entity) {
        return switch (this) {
            case AFRAID ->  List.of(new AvoidEntityGoal<>(entity, Player.class, 8.0F, 1.2, 3, EntitySelector.NO_SPECTATORS));
            case AGGRESSIVE, NEUTRAL -> List.of(new MeleeAttackGoal(entity, 1.0, false));
        };
    }

    @Override
    public MapCodec<? extends Behavior> codec() {
        return CODEC;
    }

    @Override
    public List<TargetGoal> createTargetGoals(CreatureEntity entity) {
        return switch (this) {
            case NEUTRAL -> List.of(new HurtByTargetGoal(entity));
            case AFRAID -> List.of();
            case AGGRESSIVE -> List.of(new NearestAttackableTargetGoal<>(entity, Player.class, true));
        };
    }

    @Override
    public String getSerializedName() {
        return description;
    }
}
