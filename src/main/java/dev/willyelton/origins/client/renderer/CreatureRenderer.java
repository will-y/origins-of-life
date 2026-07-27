package dev.willyelton.origins.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.origins.client.model.CreatureModel;
import dev.willyelton.origins.client.model.CreatureModels;
import dev.willyelton.origins.client.model.ModelGenerator;
import dev.willyelton.origins.common.entity.CreatureEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class CreatureRenderer extends LivingEntityRenderer<CreatureEntity, CreatureRenderState, CreatureModel> {

    public static final Identifier TEXTURE = rl("textures/entity/creature/temp.png");

    public CreatureRenderer(EntityRendererProvider.Context context) {
        super(context, new CreatureModel(ModelGenerator.defaultModel()), 0.5F);
//        this.addLayer(new CreatureRenderLayer(this, context.getModelSet()));
    }

    @Override
    public Identifier getTextureLocation(CreatureRenderState state) {
        return TEXTURE;
    }

    @Override
    public CreatureRenderState createRenderState() {
        return new CreatureRenderState();
    }

    @Override
    public void extractRenderState(CreatureEntity entity, CreatureRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entityData = entity.entityData();

    }

    @Override
    public void submit(CreatureRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        this.model = new CreatureModel(CreatureModels.getRoot(state.entityData));
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}