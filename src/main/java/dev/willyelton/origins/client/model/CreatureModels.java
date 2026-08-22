package dev.willyelton.origins.client.model;

import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;

public class CreatureModels {
    private static final Map<EntityData.ModelData, ModelPart> CACHED_MODEL_PARTS = new HashMap<>();

    public static ModelPart getRoot(EntityData.ModelData data) {
        if (CACHED_MODEL_PARTS.containsKey(data)) {
            return CACHED_MODEL_PARTS.get(data);
        }

        ModelPart baked = ModelGenerator.generateModel(data);
        CACHED_MODEL_PARTS.put(data, baked);
        return baked;
    }
}
