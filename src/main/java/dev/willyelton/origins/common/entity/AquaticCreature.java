package dev.willyelton.origins.common.entity;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.entity.data.EntityBreeder;
import dev.willyelton.origins.common.entity.data.EntityData;
import dev.willyelton.origins.common.entity.data.EntityDataGenerator;
import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jspecify.annotations.Nullable;

public class AquaticCreature extends CreatureEntity {
    public AquaticCreature(EntityType<? extends AquaticCreature> type, Level level) {
        this(level, EntityDataGenerator.random(level));
    }

    public AquaticCreature(Level level, EntityData entityData) {
        super(OriginsOfLife.AQUATIC_CREATURE.get(), level, entityData);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new AquaticCreatureMoveControl<>(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25, i -> i.is(entityData().foodTag()), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.25));

        int prio = 4;
        int targetPrio = 0;
        for (Behavior behavior : this.entityData().behaviors()) {
            for (Goal goal : behavior.createGoals(this)) {
                this.goalSelector.addGoal(prio++, goal);
            }

            for (TargetGoal targetGoal : behavior.createTargetGoals(this)) {
                this.targetSelector.addGoal(targetPrio++, targetGoal);
            }
        }

        this.goalSelector.addGoal(prio, new RandomSwimmingGoal(this, 1, 40));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        this.moveRelative(0.01F, input);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
        }
    }

    @Override
    public void baseTick() {
        int airSupply = this.getAirSupply();
        super.baseTick();
        if (this.level() instanceof ServerLevel serverLevel) {
            this.handleAirSupply(serverLevel, airSupply);
        }
    }

    protected void handleAirSupply(ServerLevel level, int preTickAirSupply) {
        if (this.isAlive() && !this.isInWater()) {
            this.setAirSupply(preTickAirSupply - 1);
            if (this.shouldTakeDrowningDamage()) {
                this.setAirSupply(0);
                this.hurtServer(level, this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public boolean isPushedByFluid(FluidType fluidType) {
        return false;
    }

    @Override
    public @Nullable AquaticCreature getBreedOffspring(ServerLevel level, AgeableMob partner) {
        if (partner instanceof AquaticCreature aquaticCreature) {
            EntityData entityData = EntityBreeder.breed(this.entityData(), aquaticCreature.entityData(), level.getRandom());
            return new AquaticCreature(level, entityData);
        }

        return null;
    }

    private static class AquaticCreatureMoveControl<T extends AquaticCreature> extends MoveControl<T> {
        public AquaticCreatureMoveControl(T creature) {
            super(creature);
        }

        @Override
        public void tick() {
            if (this.mob.isEyeInFluid(FluidTags.WATER)) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
                float targetSpeed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                this.mob.setSpeed(Mth.lerp(0.125F, this.mob.getSpeed(), targetSpeed));
                double xd = this.wantedX - this.mob.getX();
                double yd = this.wantedY - this.mob.getY();
                double zd = this.wantedZ - this.mob.getZ();
                if (yd != 0.0) {
                    double dd = Math.sqrt(xd * xd + yd * yd + zd * zd);
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, this.mob.getSpeed() * (yd / dd) * 0.1, 0.0));
                }

                if (xd != 0.0 || zd != 0.0) {
                    float yRotD = (float)(Mth.atan2(zd, xd) * 180.0F / (float)Math.PI) - 90.0F;
                    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yRotD, 90.0F));
                    this.mob.yBodyRot = this.mob.getYRot();
                }
            } else {
                this.mob.setSpeed(0.0F);
            }
        }
    }
}
