package dev.willyelton.origins.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.willyelton.origins.client.model.CreatureModel;
import dev.willyelton.origins.client.model.CreatureModels;
import dev.willyelton.origins.client.model.ModelGenerator;
import dev.willyelton.origins.common.entity.CreatureEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class CreatureRenderer extends MobRenderer<CreatureEntity, CreatureRenderState, CreatureModel> {

    public static final Identifier TEXTURE = rl("textures/entity/creature/background.png");

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
        ModelPart root = CreatureModels.getRoot(state.entityData.modelData());
        this.model = new CreatureModel(root.getChild("main"));

        super.submit(state, poseStack, submitNodeCollector, camera);

        Model<CreatureRenderState> eyeModel = new CreatureModel(root.getChild("eyes"));

        // TODO: This is bad and should mixin or something else
        poseStack.pushPose();
        float scale = state.scale;
        poseStack.scale(scale, scale, scale);
        this.setupRotations(state, poseStack, state.bodyRot, scale);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(state, poseStack);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        boolean isBodyVisible = this.isBodyVisible(state);
        boolean forceTransparent = !isBodyVisible && !state.isInvisibleToPlayer;
        RenderType renderType = this.getRenderType(state, isBodyVisible, forceTransparent, true);
        if (renderType != null) {
            int overlayCoords = getOverlayCoords(state, this.getWhiteOverlayProgress(state));
            int baseColor = forceTransparent ? 654311423 : -1;
            int tintedColor = ARGB.multiply(baseColor, state.entityData.eyeColor());
            submitNodeCollector.submitModel(
                    eyeModel, state, poseStack, renderType, state.lightCoords, overlayCoords, tintedColor, null, state.outlineColor, null
            );
        }
        eyeModel.setupAnim(state);
        poseStack.popPose();
    }

    @Override
    protected int getModelTint(CreatureRenderState state) {
        return state.entityData.color();
    }
}