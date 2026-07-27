package dev.willyelton.origins.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.origins.client.model.CreatureModel;
import dev.willyelton.origins.client.model.ModelGenerator;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

import static dev.willyelton.origins.client.renderer.CreatureRenderer.TEXTURE;

public class CreatureRenderLayer extends RenderLayer<LivingEntityRenderState, CreatureModel> {
    private final CreatureModel creatureModel;

    public CreatureRenderLayer(RenderLayerParent<LivingEntityRenderState, CreatureModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        creatureModel = new CreatureModel(ModelGenerator.defaultModel());
    }


    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, LivingEntityRenderState state, float yRot, float xRot) {
        submitNodeCollector.order(1).submitModel(creatureModel, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }
}
