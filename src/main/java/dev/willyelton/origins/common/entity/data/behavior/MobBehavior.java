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
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

import static dev.willyelton.origins.OriginsOfLife.rl;

public enum MobBehavior implements Behavior, StringRepresentable {
    NEUTRAL("Mob Neutral", TextColor.WHITE.getValue()),
    AFRAID("Mob Afraid", TextColor.AQUA.getValue()),
    AGGRESSIVE("Mob Aggressive", TextColor.RED.getValue());

    public static final MapCodec<MobBehavior> CODEC = StringRepresentable.fromEnum(MobBehavior::values).fieldOf("mobBehavior");
    public static final StreamCodec<FriendlyByteBuf, MobBehavior> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(MobBehavior.class);

    private final String description;
    private final int displayColor;

    MobBehavior(String description, int displayColor) {
        this.description = description;
        this.displayColor = displayColor;
    }

    @Override
    public String behaviorName() {
        return "Mob Relation";
    }

    @Override
    public Identifier identifier() {
        return rl("mob_behavior");
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
            case AFRAID ->  List.of(new AvoidEntityGoal<>(entity, AgeableWaterCreature.class, 8.0F, 1.2, 3, EntitySelector.NO_SPECTATORS));
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
            case AGGRESSIVE -> List.of(new NearestAttackableTargetGoal<>(entity, AgeableWaterCreature.class, true));
        };
    }

    @Override
    public String getSerializedName() {
        return description;
    }
}
