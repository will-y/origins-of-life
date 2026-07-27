package dev.willyelton.origins.client.event;

import dev.willyelton.origins.OriginsOfLife;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = OriginsOfLife.MODID, value = Dist.CLIENT)
public class RegisterLayerDefinitions {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        event.registerLayerDefinition(BODY_LOCATION, CreatureModel::createBodyLayer);
    }
}
