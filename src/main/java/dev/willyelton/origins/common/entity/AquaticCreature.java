package dev.willyelton.origins.common.entity;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.entity.data.EntityBreeder;
import dev.willyelton.origins.common.entity.data.EntityData;
import dev.willyelton.origins.common.entity.data.EntityDataGenerator;
import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
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
        this.moveControl = new SmoothSwimmingMoveControl<>(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isNoAi() && !this.isInWater() && this.onGround()) {
            this.setDeltaMovement(
                    this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F, 0.5, (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)
            );
            this.setYRot(this.random.nextFloat() * 360.0F);
            this.setOnGround(false);
            this.needsSync = true;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
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

        this.goalSelector.addGoal(prio, new RandomSwimmingGoal(this, 1, 10));
        this.goalSelector.addGoal(prio, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(++prio, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        this.moveRelative(this.getSpeed(), input);
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
    public boolean canAttack(LivingEntity target) {
        return !this.isBaby() && super.canAttack(target);
    }

    @Override
    public @Nullable AquaticCreature getBreedOffspring(ServerLevel level, AgeableMob partner) {
        if (partner instanceof AquaticCreature aquaticCreature) {
            EntityData entityData = EntityBreeder.breed(this.entityData(), aquaticCreature.entityData(), level.getRandom());
            return new AquaticCreature(level, entityData);
        }

        return null;
    }
}
