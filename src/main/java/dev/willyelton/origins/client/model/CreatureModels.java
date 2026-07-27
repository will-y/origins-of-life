package dev.willyelton.origins.client.model;

import dev.willyelton.origins.common.entity.EntityData;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;

public class CreatureModels {
    private static final Map<EntityData, ModelPart> CACHED_MODEL_PARTS = new HashMap<>();

    public static ModelPart getRoot(EntityData data) {
        if (CACHED_MODEL_PARTS.containsKey(data)) {
            return CACHED_MODEL_PARTS.get(data);
        }

        ModelPart baked = ModelGenerator.generateModel(data);
        CACHED_MODEL_PARTS.put(data, baked);
        return baked;
    }
}
