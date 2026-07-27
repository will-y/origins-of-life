package dev.willyelton.origins.client.event;

import dev.willyelton.origins.OriginsOfLife;
import dev.willyelton.origins.client.renderer.CreatureRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = OriginsOfLife.MODID, value = Dist.CLIENT)
public class RegisterEntityRenderersEvent {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(OriginsOfLife.AQUATIC_CREATURE.get(), CreatureRenderer::new);
    }
}
