package dev.willyelton.origins.client.model;

import dev.willyelton.origins.common.entity.EntityData;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Takes in entity data and creates the model
public class ModelGenerator {

    public static ModelPart generateModel(@Nullable EntityData entityData) {
        if (entityData == null) {
            return DEFAULT_MODEL;
        }

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        generateBody(entityData, partDefinition);

        return LayerDefinition.create(meshDefinition, 512, 512).bakeRoot();
    }

    private static void generateBody(EntityData entityData, PartDefinition partDefinition) {
        List<EntityData.CubeSegment> bodySegments = entityData.bodySegments();
        List<CubeListBuilder> bodyBuilders = entityData.bodySegments().stream().map(x -> new CubeListBuilder()).toList();
        generateCubes(bodyBuilders, bodySegments, entityData.sizes());
        PartDefinition body = partDefinition.addOrReplaceChild("body", new CubeListBuilder(), PartPose.rotation(0, -Mth.PI / 2.0F, 0));

        for (CubeListBuilder cubeListBuilder : bodyBuilders) {
            body = body.addOrReplaceChild("body_segment", cubeListBuilder, PartPose.ZERO);
        }
    }

    // TODO: These each need to be separate model parts for animation
    private static void generateCubes(List<CubeListBuilder> builders, List<EntityData.CubeSegment> cubeSegments, EntityData.Sizes sizes) {
        if (cubeSegments.isEmpty()) {
            return;
        }

        int currentX = 0;
        int prevY = cubeSegments.getFirst().y();
        float prevYOffset = 0;
        int prevZ = cubeSegments.getFirst().z();
        float prevZOffset = 0;

        for (int i = 0; i < cubeSegments.size(); i++) {
            EntityData.CubeSegment segment = cubeSegments.get(i);
            float newYOffset = prevYOffset + prevY / 2.0F - segment.y() / 2.0F;
            float newZOffset = prevZOffset + prevZ / 2.0F - segment.z() / 2.0F;
            builders.get(i).texOffs(5, 5)
                    .addBox(currentX - sizes.maxX() / 2.0F, newYOffset, newZOffset - sizes.maxZ() / 2.0F, segment.x(), segment.y(), segment.z());
            currentX += segment.x();
            prevY = segment.y();
            prevZ = segment.z();
            prevYOffset = newYOffset;
            prevZOffset = newZOffset;
        }
    }

    ///  Fallback if there isn't an entity yet
    public static ModelPart defaultModel() {
        return DEFAULT_MODEL;
    }


    private static final ModelPart DEFAULT_MODEL;

    static {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition main = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-10, -10, -10, 10, 10, 10),
                PartPose.offset(0, 8, 0));

        DEFAULT_MODEL = LayerDefinition.create(mesh, 64, 32).bakeRoot();
    }
}
