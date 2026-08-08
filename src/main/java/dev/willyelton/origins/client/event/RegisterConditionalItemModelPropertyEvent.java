package dev.willyelton.origins.client.event;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.client.model.property.FossilProperty;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static dev.willyelton.origins.OriginsOfLife.rl;

@EventBusSubscriber(modid = OriginsOfLife.MODID, value = Dist.CLIENT)
public class RegisterConditionalItemModelPropertyEvent {
    @SubscribeEvent
    public static void registerConditionalProperties(net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent event) {
        event.register(rl("fossil"), FossilProperty.MAP_CODEC);
    }
}