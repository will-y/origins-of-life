package dev.willyelton.origins.client;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = OriginsOfLife.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OriginsOfLife.MODID, value = Dist.CLIENT)
public class OriginsofLifeClient {
    public OriginsofLifeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        OriginsOfLife.LOGGER.info("HELLO FROM CLIENT SETUP");
        OriginsOfLife.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
