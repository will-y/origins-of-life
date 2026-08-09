package dev.willyelton.origins.common.event;

import dev.willyelton.origins.Config;
import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.common.DataComponents;
import dev.willyelton.origins.common.entity.AquaticCreature;
import dev.willyelton.origins.common.entity.EntityDataGenerator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

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

            if (!level.isClientSide()) {
                if (progress >= Config.FOSSIL_TRANSFORM_TICKS.get()) {
                    AquaticCreature creature = new AquaticCreature(level, stack.getOrDefault(DataComponents.ENTITY_DATA, EntityDataGenerator.random()));
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
