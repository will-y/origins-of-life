package dev.willyelton.origins.client.renderer;

import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CreatureRenderState extends LivingEntityRenderState {
    public EntityData entityData;
    public boolean inCage;
}
