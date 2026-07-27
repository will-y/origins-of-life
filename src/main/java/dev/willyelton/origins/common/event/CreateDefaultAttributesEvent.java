package dev.willyelton.origins.common.event;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = OriginsOfLife.MODID)
public class CreateDefaultAttributesEvent {
    @SubscribeEvent
    public static void createDefaultAttributes(final EntityAttributeCreationEvent event) {
        event.put(OriginsOfLife.AQUATIC_CREATURE.get(), AbstractFish.createAttributes().build());
    }
}
