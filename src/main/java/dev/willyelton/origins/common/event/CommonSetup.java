package dev.willyelton.origins.common.event;

import dev.willyelton.origins.OriginsOfLife;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.transfer.fluid.DispenseFluidContainer;

@EventBusSubscriber(modid = OriginsOfLife.MODID)
public class CommonSetup {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(OriginsOfLife.PRIMORDIAL_SOUP_BUCKET, DispenseFluidContainer.getInstance()));
    }
}
