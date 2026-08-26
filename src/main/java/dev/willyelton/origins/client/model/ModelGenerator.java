package dev.willyelton.origins.client.model;

import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/// Takes in entity data and creates the model
public class ModelGenerator {

    public static ModelPart generateModel(EntityData.@Nullable ModelData entityData) {
        if (entityData == null) {
            return DEFAULT_MODEL;
        }

        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        generateBody(entityData, partDefinition);

        return LayerDefinition.create(meshDefinition, 512, 512).bakeRoot();
    }

    private static void generateBody(EntityData.ModelData entityData, PartDefinition partDefinition) {
        // Keep these not under main so I can color them differently
        PartDefinition eyes = partDefinition.addOrReplaceChild("eyes", new CubeListBuilder(), PartPose.rotation(0, -Mth.PI / 2.0F, 0));

        PartDefinition main = partDefinition.addOrReplaceChild("main", new CubeListBuilder(), PartPose.rotation(0, -Mth.PI / 2.0F, 0));
        // Head + body
        List<PartDefinition> allBodyParts = new ArrayList<>();

        // Head
        CubeListBuilder headBuilder = new CubeListBuilder();
        generateCubes(List.of(headBuilder), List.of(entityData.head()), entityData.sizes());

        allBodyParts.add(main.addOrReplaceChild("head", headBuilder, PartPose.rotation(0, 0, 0)));

        // Body
        List<EntityData.CubeSegment> bodySegments = entityData.bodySegments();
        List<CubeListBuilder> bodyBuilders = new ArrayList<>(bodySegments.stream().map(_ -> new CubeListBuilder()).toList());
        generateCubes(bodyBuilders, bodySegments, entityData.sizes());
        CubeListBuilder firstBodyBuilder = bodyBuilders.isEmpty() ? new CubeListBuilder() :  bodyBuilders.removeFirst();
        PartDefinition body = main.addOrReplaceChild("body", firstBodyBuilder,
                PartPose.offsetAndRotation(0,0, 0,
                        0, 0, 0));
        allBodyParts.add(body);

        for (CubeListBuilder cubeListBuilder : bodyBuilders) {
            body = body.addOrReplaceChild("body_segment", cubeListBuilder, PartPose.ZERO);
            allBodyParts.add(body);
        }

        // Decorations
        entityData.decorations().forEach((key, val) -> val.forEach(cubeSegment -> {
            CubeListBuilder builder = CubeListBuilder.create()
                    .texOffs(cubeSegment.u(), cubeSegment.v())
                    .addBox(0, 0,0, cubeSegment.x(), cubeSegment.y(), cubeSegment.z());
            if (key >= allBodyParts.size()) {
                throw new IllegalArgumentException("Invalid decoration index: " + key + " for " + allBodyParts.size() + " body segments");
            }

            if (cubeSegment.name() == null || cubeSegment.name().isEmpty()) {
                throw new IllegalArgumentException("Decoration must have a name");
            }

            PartPose partPose = PartPose.offsetAndRotation(cubeSegment.x0() - entityData.sizes().centerX(), 24 + cubeSegment.y0(), cubeSegment.z0() - entityData.sizes().centerZ(), cubeSegment.xRot(), cubeSegment.yRot(), cubeSegment.zRot());
            // Use this for unattached decorations. For know hard coded to eyes
            if (key == -1) {
                eyes.addOrReplaceChild(cubeSegment.name(), builder, partPose);
            } else {
                allBodyParts.get(key).addOrReplaceChild(cubeSegment.name(), builder, partPose);
            }
        }));
    }

    private static void generateCubes(List<CubeListBuilder> builders, List<EntityData.CubeSegment> cubeSegments, EntityData.Sizes sizes) {
        if (cubeSegments.isEmpty()) {
            return;
        }

        for (int i = 0; i < cubeSegments.size(); i++) {
            EntityData.CubeSegment segment = cubeSegments.get(i);
            // WTF minecraft
            builders.get(i).texOffs(segment.u(), segment.v())
                    .addBox(segment.x0() - sizes.centerX(), 24 - segment.y() - segment.y0(), segment.z0() - sizes.centerZ(), segment.x(), segment.y(), segment.z());
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
        root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-10, -10, -10, 10, 10, 10),
                PartPose.offset(0, 8, 0));

        DEFAULT_MODEL = LayerDefinition.create(mesh, 64, 32).bakeRoot();
    }
}
