package dev.willyelton.origins.common.entity.data;

import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import dev.willyelton.origins.common.entity.data.behavior.PlayerBehavior;
import dev.willyelton.origins.util.random.Distribution;
import dev.willyelton.origins.util.random.IntegerNormalDistribution;
import dev.willyelton.origins.util.random.NormalDistribution;
import dev.willyelton.origins.util.random.WeightedDistribution;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Put all probabilities in the config
public class EntityDataGenerator {
    private static final int MAX_BASE_BODY_SIZE = 25;
    private static final int MIN_BASE_BODY_SIZE = 5;
    private static final int ABSOLUTE_MIN_BODY_SIZE = 3;
    private static final int[] BODY_COUNT_WEIGHTS = new int[]{0, 75, 20, 10, 5, 4, 3, 2, 1, 1, 1};
    // -3
    private static final int[] BODY_NEXT_X_SIZE_WEIGHTS = new int[]{60, 70, 80, 90, 10};
    // -3
    private static final int[] BODY_NEXT_Y_Z_SIZE_WEIGHTS = new int[]{60, 70, 80, 90, 10};
    private static final int[] COLORS = new int[] {
            -15590866, // Abyssal Navy (0xFF121A2E)
            -15193275, // Deep Trench (0xFF182B45)
            -15844541, // Midnight Cyan (0xFF0E3B43)
            -15379879, // Oceanic Teal (0xFF155259)
            -15037282, // Vibrant Aqua (0xFF1A8C9E)
            -13065307, // Seafoam Glow (0xFF38A3A5)
            -13809118, // Kelp Green (0xFF2D4A22)
            -2729342,  // Coral Reef Pink (0xFFD65A82)
            -13492674, // Abyssal Purple (0xFF321E3E)
            -14522676, // Biolum Blue (0xFF2266CC)
            -2566967,  // Stained Bone (0xFFD8D4C9)
            -15128511, // Deep Sea Blue (0xFF192841)
            -15441040, // Tidal Cyan (0xFF146370)
            -13945797, // Phantom Grey (0xFF2B343B)
            -16045264,  // Glowsquid Dark (0xFF0B2B30)
            -16777216, // Abyssal Black (0xFF000000)
            -12105913, // Slate Grey (0xFF474747)
            -7829368,  // Pale Ash Grey (0xFF888888)
            -21444,    // Bioluminescent Yellow (0xFFFFAB1C)
            -3404746   // Angler Crimson Red (0xFFCC0836)
    };

    private static final int[] EYE_COLORS = {
            0xFF00F5FF, // Glow Cyan
            0xFF0A1172, // Deep Sea Blue
            0xFFFF1493, // Bioluminescent Pink
            0xFF00FF7F, // Acid Green
            0xFF4B0082, // Abyssal Violet
            0xFFFFD700, // Golden Coral
            0xFF008B8B, // Toxic Teal
            0xFF00FFFF, // Electric Aqua
            0xFFE0FFFF, // Ghost White Blue
            0xFFFF8C00, // Amber Lantern
            0xFFFFFFFF, // Pure White
            0xFF000000,  // Pure Black
            -3404746   // Angler Crimson Red (0xFFCC0836)
    };

    public static EntityData empty() {
        return new EntityData(new EntityData.CubeSegment(0, 0, 0, 1, 1, 1, 0, 0), new ArrayList<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), -1, -1, List.of());
    }

    public static EntityData random(Level level) {
        RandomSource rand = randomSource(level);
        Distribution<Integer> bodyCountDistribution = new WeightedDistribution(rand, BODY_COUNT_WEIGHTS);
        Distribution<Integer> bodyXSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_X_SIZE_WEIGHTS);
        Distribution<Integer> bodyYZSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_Y_Z_SIZE_WEIGHTS);

        int bodySegmentCount = bodyCountDistribution.nextValue();

        List<EntityData.CubeSegment> bodySegments = new ArrayList<>();

        var bodyUV = generateUV(rand);
        EntityData.CubeSegment head = EntityData.CubeSegment.randomSquare(rand, MIN_BASE_BODY_SIZE, MAX_BASE_BODY_SIZE, bodyUV.getLeft(), bodyUV.getRight());
        EntityData.CubeSegment current = head;

        for (int i = 0; i < bodySegmentCount; i++) {
            current = nextSegment(current, bodyXSizeDistribution, bodyYZSizeDistribution, rand, bodyUV);
            bodySegments.add(current);
        }

        Map<Integer, List<EntityData.CubeSegment>> decorations = new HashMap<>();
        // Body + Head
        List<EntityData.CubeSegment> allBodySegments = new ArrayList<>();
        allBodySegments.add(head);
        allBodySegments.addAll(bodySegments);
        generateFins(rand, allBodySegments, decorations);
        generateHeadFeatures(rand, head, decorations);
        generateEyes(rand, head, decorations);
        if (!bodySegments.isEmpty()) {
            generateTailFeatures(rand, bodySegments.getLast(), bodySegments.size(), decorations);
        }

        return new EntityData(head, bodySegments, decorations, generateAnimationData(rand), generateDefaultAttributes(rand),
                randomElement(rand, COLORS), randomElement(rand, EYE_COLORS), generateBehaviors(rand));
    }

    private static EntityData.CubeSegment nextSegment(EntityData.CubeSegment current, Distribution<Integer> xDistribution,
                                                      Distribution<Integer> yZDistribution, RandomSource rand, Pair<Integer, Integer> uv) {
        int dX = xDistribution.nextValue() - 3;
        int dY = yZDistribution.nextValue() - 3;
        int dZ = yZDistribution.nextValue() - 3;
        int x = Math.max(current.x() + dX, ABSOLUTE_MIN_BODY_SIZE);
        int y = Math.max(current.y() + dY, ABSOLUTE_MIN_BODY_SIZE);
        int z = Math.max(current.z() + dZ, ABSOLUTE_MIN_BODY_SIZE);

        return new EntityData.CubeSegment(current.x0() + current.x(),
                current.y0() + current.y() / 2.0F - y / 2.0F,
                current.z0() + current.z() / 2.0F - z / 2.0F,
                x, y, z, uv.getLeft(), uv.getRight());
    }

    private static RandomSource randomSource(Level level) {
        if (level != null) {
            return level.getRandom();
        }

        return RandomSource.create();
    }

    private static void generateFins(RandomSource rand, List<EntityData.CubeSegment> bodySegments, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        var uv = generateUV(rand);

        // Top fins
        if (rand.nextFloat() > 0.3) {
            float zRot = (rand.nextInt(20, 90)) * Mth.PI / 180.0F;
            for (int i = 0; i < bodySegments.size(); i++) {
                if (rand.nextFloat() > 0.1) {
                    EntityData.CubeSegment segment = bodySegments.get(i);
                    int y = rand.nextInt(1, segment.x() * 2 / 3 + 1);
                    int z = rand.nextInt(1, segment.z() / 3 + 1);
                    int x = rand.nextInt(Math.min(y, z), Math.max(y, z) + 1);
                    decorations.computeIfAbsent(i, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(segment.x0() + segment.x() / 2.0F - x / 2.0F + Mth.sin(zRot) * x,
                            segment.y0() - Mth.cos(zRot) * Mth.sqrt(y * y + z * z),
                            segment.z0() + segment.z() / 2.0F - z / 2.0F,
                            x, y, z, 0, 0, zRot,
                            uv.getLeft(), uv.getRight(), "top_fin"));
                }
            }
        }

        // Side fins
        if (rand.nextFloat() > 0.4) {
            for (int i = 0; i < bodySegments.size(); i++) {
                if (rand.nextFloat() > 0.3) {
                    EntityData.CubeSegment segment = bodySegments.get(i);
                    int x = Math.max(rand.nextInt(segment.x() / 4, segment.x() * 3 / 4), 1);
                    int y = rand.nextInt(1, 4);
                    int z = rand.nextInt(Math.min(3, segment.z() / 3), Math.max(segment.z(), 6));

                    float x0 = segment.x0() + segment.x() / 2.0F - x / 2.0F;
                    float y0 = segment.y0() + segment.y() / 2.0F - y / 2.0F;

                    decorations.computeIfAbsent(i, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(x0 + x, y0, segment.z0(), x, y, z, 0, Mth.PI, 0, uv.getLeft(), uv.getRight(), "left_fin"));
                    decorations.computeIfAbsent(i, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(x0, y0, segment.z0() + segment.z(), x, y, z, uv.getLeft(), uv.getRight(), "right_fin"));
                }
            }
        }
    }

    private static void generateHeadFeatures(RandomSource rand, EntityData.CubeSegment head, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        if (rand.nextFloat() > 0.3) {
            IntegerNormalDistribution noseXDistribution = new IntegerNormalDistribution(rand, 4, 3);
            IntegerNormalDistribution noseYZDistribution = new IntegerNormalDistribution(rand, 3, 4);
            // Nose
            int x = Mth.clamp(noseXDistribution.nextValue(), 1, head.x());
            int y = Mth.clamp(noseYZDistribution.nextValue(), 1, head.y() / 2);
            int z = Mth.clamp(noseYZDistribution.nextValue(), 1, head.z() / 2);
            float yOffset = rand.nextFloat() * (head.y() / 2.0F - y / 2.0F) + head.y() / 2.0F - y / 2.0F;
            float y0 = head.y0() + yOffset;
            var uv = generateUV(rand);

            decorations.computeIfAbsent(0,_ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(head.x0() - x, y0,
                    head.z0() + head.z() / 2.0F - z / 2.0F,
                    x, y, z, uv.getLeft(), uv.getRight(), "nose"));
        }
    }

    private static void generateTailFeatures(RandomSource rand, EntityData.CubeSegment lastBodySegment, int lastBodySegmentIndex, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        // TODO: Tails should make sense (if hor. swim, vertical tail...)
        if (rand.nextFloat() > 0.6) {
            // Flat tail
            IntegerNormalDistribution tailXDistribution = new IntegerNormalDistribution(rand, lastBodySegment.x(), lastBodySegment.x() / 2.0F);
            IntegerNormalDistribution tailYDistribution = new IntegerNormalDistribution(rand, 2, 1);
            IntegerNormalDistribution tailZDistribution = new IntegerNormalDistribution(rand, lastBodySegment.z(), lastBodySegment.z() / 2.0F);

            int x = Math.max(tailXDistribution.nextValue(), 3);
            int y = Mth.clamp(tailYDistribution.nextValue(), 1, lastBodySegment.y());
            int z = Mth.clamp(tailZDistribution.nextValue(), 1, lastBodySegment.z() * 2);
            var uv = generateUV(rand);

            decorations.computeIfAbsent(lastBodySegmentIndex, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(
                    lastBodySegment.x0() + lastBodySegment.x(),
                    lastBodySegment.y0() + lastBodySegment.y() / 2.0F - y / 2.0F,
                    lastBodySegment.z0() + lastBodySegment.z() / 2.0F - z / 2.0F,
                    x, y, z, uv.getLeft(), uv.getRight(), "tail"));
        }

        // 2 Pronged Tail
        // TODO
    }

    // TODO: Sometimes eyes can overlay with noses
    private static void generateEyes(RandomSource rand, EntityData.CubeSegment head, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        if (rand.nextFloat() > 0.1) {
            Distribution<Integer> eyeXZDistribution = new IntegerNormalDistribution(rand, 4, 1);
            Distribution<Integer> eyeXDistribution = new WeightedDistribution(rand, new int[] {0, 75, 15, 10});
            int x = Mth.clamp(eyeXDistribution.nextValue(), 1, head.x() / 2);
            var uv = generateUV(rand);

            // One eye
            if (rand.nextFloat() > 0.8) {
                int yz = Mth.clamp(eyeXZDistribution.nextValue() * 2, 1, head.z() / 3);
                float yOffset = rand.nextFloat() * (head.y() / 3.0F - yz / 2.0F);
                float y0 = head.y0() + yOffset;

                decorations.computeIfAbsent(-1, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(head.x0() - x,
                        y0, head.z0() + head.z() / 2.0F - yz / 2.0F, x, yz, yz, uv.getLeft(), uv.getRight(), "eye_1"));
            } else {
                // Two eyes
                int yz = Mth.clamp(eyeXZDistribution.nextValue(), 1, head.x() / 3);
                float yOffset = rand.nextFloat() * (head.y() / 2.0F - yz / 2.0F) - 2;
                float y0 = head.y0() + yOffset;

                decorations.computeIfAbsent(-1, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(head.x0() - x,
                        y0, head.z0() + head.z() / 2.0F - yz / 2.0F - head.z() / 4.0F, x, yz, yz, uv.getLeft(), uv.getRight(), "eye_1"));
                decorations.computeIfAbsent(-1, _ -> new ArrayList<>(1)).add(new EntityData.CubeSegment(head.x0() - x,
                        y0, head.z0() + head.z() / 2.0F - yz / 2.0F + head.z() / 4.0F, x, yz, yz, uv.getLeft(), uv.getRight(), "eye_2"));
            }
        }
    }

    private static Map<String, Float> generateAnimationData(RandomSource rand) {
        Map<String, Float> animationData = new HashMap<>();
        NormalDistribution dist = new NormalDistribution(rand, 1, 0.2F);

        animationData.put("AQUATIC_DIRECTION", rand.nextFloat() > 0.3 ? 1.0F : 0);
        animationData.put("AQUATIC_BASE_ANGLE_MULTIPLIER", Math.max(dist.nextValue(), 0.01F));
        animationData.put("AQUATIC_BASE_AMPLITUDE_MULTIPLIER", Math.max(dist.nextValue(), 0.01F));

        return animationData;
    }

    private static Map<Holder<Attribute>, Double> generateDefaultAttributes(RandomSource rand) {
        HashMap<Holder<Attribute>, Double> defaultAttributes = new HashMap<>();
        defaultAttributes.put(Attributes.ARMOR, generateAttribute(rand, 3, 1));
        defaultAttributes.put(Attributes.ARMOR_TOUGHNESS, generateAttribute(rand, 2, 1));
        defaultAttributes.put(Attributes.ATTACK_DAMAGE, generateAttribute(rand, 5, 2, 0.5F));
        defaultAttributes.put(Attributes.ATTACK_KNOCKBACK, generateAttribute(rand, 0.5F, 0.1F));
        defaultAttributes.put(Attributes.KNOCKBACK_RESISTANCE, generateAttribute(rand, 0.2F, 0.05F));
        defaultAttributes.put(Attributes.MAX_HEALTH, generateAttribute(rand, 15, 5, 0.5F));
        defaultAttributes.put(Attributes.MOVEMENT_SPEED, generateAttribute(rand, 1, 0.5F, 0.1F));
        defaultAttributes.put(Attributes.SCALE, generateAttribute(rand, 1, 0.3F, 0.1F));
        return defaultAttributes;
    }

    private static double generateAttribute(RandomSource rand, float mean, float variance) {
        return generateAttribute(rand, mean, variance, 0);
    }

    private static double generateAttribute(RandomSource rand, float mean, float variance, float floor) {
        NormalDistribution distribution = new NormalDistribution(rand, mean, variance);
        return Math.max(distribution.nextValue(), floor);
    }

    private static Pair<Integer, Integer> generateUV(RandomSource rand) {
        int pallet = rand.nextInt(4);
        int uCenter;
        int vCenter;

        switch (pallet) {
            case 0 -> {
                uCenter = 0;
                vCenter = 0;
            }
            case 1 -> {
                uCenter = 256;
                vCenter = 0;
            }
            case 2 -> {
                uCenter = 0;
                vCenter = 256;
            }
            case 3 -> {
                uCenter = 256;
                vCenter = 256;
            }
            default -> throw new IllegalArgumentException("Invalid pallet");
        }

        int u = rand.nextIntBetweenInclusive(uCenter, uCenter + 64);
        int v = rand.nextIntBetweenInclusive(vCenter, vCenter + 64);

        return Pair.of(u, v);
    }

    private static int randomElement(RandomSource rand, int[] array) {
        int index = rand.nextInt(array.length);
        return array[index];
    }

    private static List<Behavior> generateBehaviors(RandomSource rand) {
        List<Behavior> behaviors = new ArrayList<>();

        WeightedDistribution dist = new WeightedDistribution(rand, new int[] {60, 20, 20});

        int playerBehavior = dist.nextValue();

        switch (playerBehavior) {
            case 0 -> behaviors.add(PlayerBehavior.NEUTRAL);
            case 1 -> behaviors.add(PlayerBehavior.AFRAID);
            case 2 -> behaviors.add(PlayerBehavior.AGGRESSIVE);
        }

        return behaviors;
    }
}
