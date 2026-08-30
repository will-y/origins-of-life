package dev.willyelton.origins.common.event;

import dev.willyelton.origins.Config;
import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.entity.AquaticCreature;
import dev.willyelton.origins.common.entity.data.EntityData;
import dev.willyelton.origins.common.entity.data.EntityDataGenerator;
import dev.willyelton.origins.common.entity.data.behavior.PlayerBehavior;
import dev.willyelton.origins.common.tag.OriginsOfLifeTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = OriginsOfLife.MODID)
public class EntityTickEvent {
    @SubscribeEvent
    public static void entityTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (entity instanceof ItemEntity itemEntity
                && itemEntity.getItem().is(OriginsOfLife.FOSSIL.get())
                && itemEntity.getItem().has(DataComponents.ENTITY_DATA)
                && entity.isInFluidType(OriginsOfLife.PRIMORDIAL_SOUP_TYPE.get())) {
            ItemStack stack = itemEntity.getItem();
            int progress = stack.getOrDefault(DataComponents.FOSSIL_TRANSFORM_COUNTER.get(), 0) + 1;
            EntityData entityData = stack.getOrDefault(DataComponents.ENTITY_DATA, EntityDataGenerator.empty());

            if (!level.isClientSide()) {
                if (level.getGameTime() % 10 == 0) {
                    boolean dataChanged = false;
                    List<Entity> otherEntities = level.getEntities(entity, AABB.ofSize(entity.position(), 2, 2, 2), e -> e.is(EntityTypes.ITEM));
                    for (Entity otherEntity : otherEntities) {
                        if (otherEntity instanceof ItemEntity otherItemEntity) {
                            boolean itemUsed = false;
                            // Speed Up
                            ItemStack otherStack = otherItemEntity.getItem();
                            if (otherStack.is(OriginsOfLifeTags.FOSSIL_SPEED_ITEMS)) {
                                progress += Config.FOSSIL_SPEED_ITEM_TICKS.get();
                                itemUsed = true;
                            }

                            // Change Color
                            if (otherStack.has(net.minecraft.core.component.DataComponents.DYE)) {
                                DyeColor color = otherStack.get(net.minecraft.core.component.DataComponents.DYE);
                                if (color != null) {
                                    int colorInt = color.getTextureDiffuseColor();
                                    entityData = entityData.withColor(colorInt);
                                    dataChanged = true;
                                    itemUsed = true;
                                }
                            }

                            // Change Behaviors
                            if (otherStack.is(OriginsOfLifeTags.FOSSIL_PLAYER_BEHAVIOR_AGGRESSIVE)) {
                                entityData = entityData.withBehavior(PlayerBehavior.AGGRESSIVE);
                                dataChanged = true;
                                itemUsed = true;
                            }

                            if (otherStack.is(OriginsOfLifeTags.FOSSIL_PLAYER_BEHAVIOR_NEUTRAL)) {
                                entityData = entityData.withBehavior(PlayerBehavior.NEUTRAL);
                                dataChanged = true;
                                itemUsed = true;
                            }

                            if (otherStack.is(OriginsOfLifeTags.FOSSIL_PLAYER_BEHAVIOR_AFRAID)) {
                                entityData = entityData.withBehavior(PlayerBehavior.AFRAID);
                                dataChanged = true;
                                itemUsed = true;
                            }

                            if (itemUsed) {
                                level.playSound(null, otherItemEntity.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
                                otherItemEntity.discard();
                            }
                        }
                    }

                    if (dataChanged) {
                        stack.set(DataComponents.ENTITY_DATA, entityData);
                    }
                }

                if (progress >= Config.FOSSIL_TRANSFORM_TICKS.get()) {
                    AquaticCreature creature = new AquaticCreature(level, stack.getOrDefault(DataComponents.ENTITY_DATA, EntityDataGenerator.random(level)));
                    creature.setPos(entity.position());
                    level.addFreshEntity(creature);
                    entity.discard();
                } else {
                    stack.set(DataComponents.FOSSIL_TRANSFORM_COUNTER.get(), progress);
                }
            }

            if (level.getGameTime() % 6 == 1) {
                RandomSource rand = level.getRandom();
                level.addParticle(ParticleTypes.FIREWORK, entity.getX(), entity.getY(), entity.getZ(), (rand.nextDouble() - 0.5) / 5.0, rand.nextDouble() / 5.0,  (rand.nextDouble() - 0.5) / 5.0);
            }
        }
    }
}
