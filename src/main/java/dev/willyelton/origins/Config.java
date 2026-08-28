package dev.willyelton.origins;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

import static dev.willyelton.origins.common.entity.data.EntityDataGenerator.DEFAULT_BODY_COLORS;
import static dev.willyelton.origins.common.entity.data.EntityDataGenerator.DEFAULT_EYE_COLORS;

public class Config {
    public static final ModConfigSpec COMMON_CONFIG;

    public static ModConfigSpec.IntValue FOSSIL_TRANSFORM_TICKS;
    public static ModConfigSpec.BooleanValue CAGE_PICKUP_OTHER_ENTITIES;

    public static ModConfigSpec.DoubleValue TOP_FIN_PROBABILITY;
    public static ModConfigSpec.DoubleValue TOP_FIN_PER_SEGMENT_PROBABILITY;
    public static ModConfigSpec.DoubleValue SIDE_FIN_PROBABILITY;
    public static ModConfigSpec.DoubleValue SIDE_FIN_PER_SEGMENT_PROBABILITY;
    public static ModConfigSpec.DoubleValue NOSE_PROBABILITY;
    public static ModConfigSpec.DoubleValue TAIL_PROBABILITY;
    public static ModConfigSpec.DoubleValue EYES_PROBABILITY;
    public static ModConfigSpec.DoubleValue ONE_EYE_PROBABILITY;

    public static ModConfigSpec.BooleanValue AVERAGE_BODY_COLOR;
    public static ModConfigSpec.BooleanValue AVERAGE_EYE_COLOR;

    public static ModConfigSpec.ConfigValue<List<? extends Integer>> BODY_COLORS;
    public static ModConfigSpec.ConfigValue<List<? extends Integer>> EYE_COLORS;

    static {
        ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
        setupConfig(configBuilder);
        COMMON_CONFIG = configBuilder.build();
    }

    private static void setupConfig(ModConfigSpec.Builder builder) {
        builder.comment("General Settings").push("general");
        CAGE_PICKUP_OTHER_ENTITIES = builder
                .comment("Allows the cage item to pick up all mobs")
                .define("cage_pickup_other_entities", false);
        FOSSIL_TRANSFORM_TICKS = builder
                .comment("Ticks that it takes to transform a fossil into a creature")
                .defineInRange("fossil_transform_ticks", 400, 1, 10000);
        builder.pop();

        builder.comment("Creature Generation Settings").push("entity_generation");
        TOP_FIN_PROBABILITY = builder.comment("Probability that a creature has top fins")
                .defineInRange("top_fin_probability", 0.7, 0, 1);
        TOP_FIN_PER_SEGMENT_PROBABILITY = builder.comment("Probability that a specific body segment has a top fin, given they have any")
                .defineInRange("top_fin_per_segment_probability", 0.9, 0, 1);
        SIDE_FIN_PROBABILITY = builder.comment("Probability that a creature has side fins")
                .defineInRange("side_fin_probability", 0.6, 0, 1);
        SIDE_FIN_PER_SEGMENT_PROBABILITY = builder.comment("Probability that a specific body segment has side fins, given they have any")
                .defineInRange("side_fin_per_segment_probability", 0.7, 0, 1);
        NOSE_PROBABILITY = builder.comment("Probability that a creature has a nose")
                .defineInRange("nose_probability", 0.7, 0, 1);
        TAIL_PROBABILITY = builder.comment("Probability that a creature has a tail")
                .defineInRange("tail_probability", 0.4, 0, 1);
        EYES_PROBABILITY = builder.comment("Probability that a creature has eyes")
                .defineInRange("eyes_probability", 0.9, 0, 1);
        ONE_EYE_PROBABILITY = builder.comment("Probability that a creature has only one eye, given it has any")
                .defineInRange("one_eye_probability", 0.2, 0, 1);

        BODY_COLORS = builder.comment("Possible Colors for Creature Bodies. Must be an integer in the form AARRGGBB")
                        .defineList("body_colors", () -> DEFAULT_BODY_COLORS, () -> -1, _ -> true);
        EYE_COLORS = builder.comment("Possible Colors for Creature Eyes. Must be an integer in the form AARRGGBB")
                .defineList("eye_colors", () -> DEFAULT_EYE_COLORS, () -> -1, _ -> true);
        builder.pop();

        builder.comment("Creature Breeding Settings").push("breeding");
        AVERAGE_BODY_COLOR = builder.comment("Average parent's colors when breeding instead of picking one")
                        .define("average_body_color", false);
        AVERAGE_EYE_COLOR = builder.comment("Average parent's eye colors when breeding instead of picking one")
                .define("average_eye_color", false);
        builder.pop();

    }
}
