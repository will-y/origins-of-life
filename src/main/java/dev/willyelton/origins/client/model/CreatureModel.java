package dev.willyelton.origins.client.model;

import dev.willyelton.origins.client.renderer.CreatureRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class CreatureModel extends EntityModel<CreatureRenderState> {
    public static final ModelLayerLocation BODY_LOCATION = new ModelLayerLocation(rl("main"), "main");

    private final ModelPart[] bodySegments;

    public CreatureModel(ModelPart root) {
        super(root);

        if (root.hasChild("body")) {
            ModelPart body = root.getChild("body");

            List<ModelPart> bodyParts = new ArrayList<>();
            while (body.hasChild("body_segment")) {
                body =  body.getChild("body_segment");
                bodyParts.add(body);
            }

            this.bodySegments = bodyParts.toArray(new ModelPart[0]);
        } else {
            this.bodySegments = new ModelPart[0];
        }
    }

    @Override
    public void setupAnim(CreatureRenderState state) {
        super.setupAnim(state);

        super.setupAnim(state);
        // TODO: Change based on movement speed
        float amplitudeMultiplier = 1.0F;
        float angleMultiplier = 1.0F;
        if (!state.isInWater) {
            amplitudeMultiplier = 1.3F;
            angleMultiplier = 1.7F;
        }

        for (int i = 0; i < this.bodySegments.length; i++) {
            ModelPart part = this.bodySegments[i];
            part.yRot = -amplitudeMultiplier * 0.25F * Mth.sin(angleMultiplier * 0.6F * state.ageInTicks + (Math.PI / 8.0F) * i) * i / 8.0F;
        }
    }
}
