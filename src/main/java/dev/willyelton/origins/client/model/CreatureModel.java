package dev.willyelton.origins.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import static dev.willyelton.origins.OriginsOfLife.rl;

public class CreatureModel extends EntityModel<EntityRenderState> {
    public static final ModelLayerLocation BODY_LOCATION = new ModelLayerLocation(rl("main"), "main");

//    private final ModelPart body;

    public CreatureModel(ModelPart root) {
        super(root);
//        this.body = root.getChild("head");
    }
}
