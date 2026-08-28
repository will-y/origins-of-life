package dev.willyelton.origins.common.entity.data;

import com.google.common.base.Suppliers;
import dev.willyelton.origins.Config;
import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import dev.willyelton.origins.util.Colors;
import dev.willyelton.origins.util.random.Distribution;
import dev.willyelton.origins.util.random.NormalDistribution;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class EntityBreeder {

    private static final Supplier<Float> ZERO = Suppliers.ofInstance(0.0F);

    /// Breeds two {@link EntityData}s together to get a child
    public static EntityData breed(EntityData parent1, EntityData parent2, RandomSource rand) {
        // Step 1: Model TODO - actually combine somehow
        EntityData.ModelData modelData = rand.nextFloat() > 0.5 ? parent1.modelData() : parent2.modelData();

        // Step 2: Animation Data
        Distribution<Float> animationDistribution = new NormalDistribution(rand, 0.1F, 0.01F);
        Map<String, Float> animationData = new HashMap<>();
        animationData.put("AQUATIC_DIRECTION", pick(parent1.animationData().get("AQUATIC_DIRECTION"), parent2.animationData().get("AQUATIC_DIRECTION"), PickMethod.RANDOM, rand, ZERO));
        animationData.put("AQUATIC_BASE_ANGLE_MULTIPLIER", pick(parent1.animationData().get("AQUATIC_DIRECTION"), parent2.animationData().get("AQUATIC_DIRECTION"), PickMethod.AVERAGE_ADD, rand, animationDistribution::nextValue));
        animationData.put("AQUATIC_BASE_AMPLITUDE_MULTIPLIER", pick(parent1.animationData().get("AQUATIC_DIRECTION"), parent2.animationData().get("AQUATIC_DIRECTION"), PickMethod.AVERAGE_ADD, rand, animationDistribution::nextValue));

        // Step 3: Attributes
        Distribution<Float> attributeDistribution = new NormalDistribution(rand, 0.01F, 0.01F);
        Map<Holder<Attribute>, Double> attributes = new HashMap<>();
        for (Holder<Attribute> attributeHolder : parent1.defaultAttributes().keySet()) {
            Double d1 = parent1.defaultAttributes().get(attributeHolder);
            Float f1 = d1 == null ? 0.0F : d1.floatValue();
            Double d2 = parent2.defaultAttributes().get(attributeHolder);
            Float f2 = d2 == null ? 0.0F : d2.floatValue();

            double result = pick(f1, f2, PickMethod.AVERAGE_PERCENT_CHANGE, rand, attributeDistribution::nextValue).doubleValue();

            attributes.put(attributeHolder, attributeHolder.value().sanitizeValue(result));
        }

        // Step 4: Color (config for average or pick)
        int color;
        if (Config.AVERAGE_BODY_COLOR.get()) {
            color = Colors.average(parent1.color(), parent2.color());
        } else {
            color = rand.nextFloat() > 0.5 ? parent1.color() : parent2.color();
        }

        // Step 5: Eye Color (config for average or pick)
        int eyeColor;
        if (Config.AVERAGE_BODY_COLOR.get()) {
            eyeColor = Colors.average(parent1.eyeColor(), parent2.eyeColor());
        } else {
            eyeColor = rand.nextFloat() > 0.5 ? parent1.eyeColor() : parent2.eyeColor();
        }

        // Step 6: Behavior: grab one of each type
        List<Behavior> behaviors = new ArrayList<>();
        List<Behavior> parentBehaviors = new ArrayList<>();
        parentBehaviors.addAll(parent1.behaviors());
        parentBehaviors.addAll(parent2.behaviors());
        Collections.shuffle(parentBehaviors);

        Set<Identifier> seenBehaviors = new HashSet<>();

        for (Behavior behavior : parentBehaviors) {
            if (seenBehaviors.add(behavior.identifier())) {
                behaviors.add(behavior);
            }
        }

        // Step 7: Food Tag
        TagKey<Item> foodTag = rand.nextFloat() > 0.5 ? parent1.foodTag() : parent2.foodTag();

        return new EntityData(modelData, animationData, attributes, color, eyeColor, behaviors, foodTag);
    }

    private static Float pick(Float p1, Float p2, PickMethod method, RandomSource rand, Supplier<Float> num) {
        float f1 = p1 == null ? 0 : p1;
        float f2 = p2 == null ? 0 : p2;

        return switch (method) {
            case RANDOM -> rand.nextFloat() > 0.5 ? f1 : f2;
            case AVERAGE -> (f1 + f2) / 2.0F;
            case AVERAGE_ADD -> (f1 + f2) / 2.0F + num.get();
            case AVERAGE_PERCENT_CHANGE -> Math.max(0.1F, ((f1 + f2) / 2.0F) * (1 + num.get()));
        };
    }

    private enum PickMethod {
        RANDOM, AVERAGE, AVERAGE_ADD, AVERAGE_PERCENT_CHANGE
    }
}
