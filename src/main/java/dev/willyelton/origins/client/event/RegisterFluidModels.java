package dev.willyelton.origins.client.event;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;

import static dev.willyelton.origins.OriginsOfLife.rl;

@EventBusSubscriber(modid = OriginsOfLife.MODID, value = Dist.CLIENT)
public class RegisterFluidModels {
    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(
                        new Material(rl("block/primordial_soup_still")),
                        new Material(rl("block/primordial_soup_flowing")),
                        new Material(rl("block/primordial_soup_overlay")),
                        null,
                        null),
                OriginsOfLife.PRIMORDIAL_SOUP, OriginsOfLife.FLOWING_PRIMORDIAL_SOUP);
    }

}
