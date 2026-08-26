package dev.willyelton.origins.client.model;

import dev.willyelton.origins.client.renderer.CreatureRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.willyelton.origins.OriginsOfLife.rl;
import static dev.willyelton.origins.common.entity.data.AnimationData.AQUATIC_BASE_AMPLITUDE_MULTIPLIER;
import static dev.willyelton.origins.common.entity.data.AnimationData.AQUATIC_BASE_ANGLE_MULTIPLIER;
import static dev.willyelton.origins.common.entity.data.AnimationData.AQUATIC_DIRECTION;

public class CreatureModel extends EntityModel<CreatureRenderState> {
    public static final ModelLayerLocation BODY_LOCATION = new ModelLayerLocation(rl("main"), "main");

    private final ModelPart[] bodySegments;
    private @Nullable ModelPart head = null;

    public CreatureModel(ModelPart root) {
        super(root);

        if (root.hasChild("head")) {
            this.head = root.getChild("head");
        }

        if (root.hasChild("body")) {
            List<ModelPart> bodyParts = new ArrayList<>();
            ModelPart body = root.getChild("body");
            bodyParts.add(body);

            while (body.hasChild("body_segment")) {
                body = body.getChild("body_segment");
                bodyParts.add(body);
            }

            this.bodySegments = bodyParts.toArray(new ModelPart[0]);
            return;
        }


        this.bodySegments = new ModelPart[0];
    }

    @Override
    public void setupAnim(CreatureRenderState state) {
        super.setupAnim(state);

        if (state.inCage) {
            return;
        }

        // TODO: Change based on movement speed
        float amplitudeMultiplier = (state.entityData.animationData().getOrDefault(AQUATIC_BASE_AMPLITUDE_MULTIPLIER, 1.0F) / 5.0F);
        float angleMultiplier = state.entityData.animationData().getOrDefault(AQUATIC_BASE_ANGLE_MULTIPLIER, 1.0F) * 2.0F;
        if (!state.isInWater) {
            amplitudeMultiplier *= 1.3F;
            angleMultiplier *= 1.7F;
        }

        for (int i = 0; i < this.bodySegments.length; i++) {
            ModelPart part = this.bodySegments[i];
            // Body rotation
            float rot = -amplitudeMultiplier * 0.25F * Mth.sin(angleMultiplier * 0.1F * state.ageInTicks + (Math.PI / 4.0F) * (i + 1)) * (i + 1) / 8.0F;

            if (state.entityData.animationData().getOrDefault(AQUATIC_DIRECTION, 0.0F) > 0) {
                part.yRot = rot;
            } else {
                part.zRot = rot;
            }

            // Side fins
            animateSideFins(part, amplitudeMultiplier, angleMultiplier, state, i);
        }

        // Head animations
        if (this.head != null) {
            animateSideFins(this.head, amplitudeMultiplier, angleMultiplier, state, -2);
        }

        // Tail animations
        if (this.bodySegments.length > 0) {
            animateTail(this.bodySegments[this.bodySegments.length - 1], amplitudeMultiplier, angleMultiplier / 2.0F, state);
        }
    }

    private void animateTail(ModelPart part, float amplitudeMultiplier, float angleMultiplier, CreatureRenderState state) {
        if (part.hasChild("tail")) {
            ModelPart tail = part.getChild("tail");
            tail.zRot = -amplitudeMultiplier * 0.45F * Mth.sin(angleMultiplier * 0.3F * state.ageInTicks);
        }
    }

    private void animateSideFins(ModelPart part, float amplitudeMultiplier, float angleMultiplier, CreatureRenderState state, int i) {
        if (part.hasChild("left_fin")) {
            animateSideFin(part.getChild("left_fin"), amplitudeMultiplier, angleMultiplier, state, i);
        }

        if (part.hasChild("right_fin")) {
            animateSideFin(part.getChild("right_fin"), amplitudeMultiplier, angleMultiplier, state, i);
        }
    }

    private void animateSideFin(ModelPart part, float amplitudeMultiplier, float angleMultiplier, CreatureRenderState state, int i) {
        part.xRot = -amplitudeMultiplier * 0.45F * Mth.sin(angleMultiplier * 0.3F * state.ageInTicks + (Math.PI / 4.0F) * (i + 1));
    }
}
