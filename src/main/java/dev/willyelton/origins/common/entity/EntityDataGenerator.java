package dev.willyelton.origins.common.entity;

import dev.willyelton.origins.util.random.Distribution;
import dev.willyelton.origins.util.random.IntegerNormalDistribution;
import dev.willyelton.origins.util.random.WeightedDistribution;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

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

    public static EntityData empty() {
        return new EntityData(new EntityData.CubeSegment(0, 0, 0, 1, 1, 1), new ArrayList<>(), new HashMap<>());
    }

    public static EntityData random(Level level) {
        RandomSource rand = randomSource(level);
        Distribution<Integer> bodyCountDistribution = new WeightedDistribution(rand, BODY_COUNT_WEIGHTS);
        Distribution<Integer> bodyXSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_X_SIZE_WEIGHTS);
        Distribution<Integer> bodyYZSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_Y_Z_SIZE_WEIGHTS);

        int bodySegmentCount = bodyCountDistribution.nextValue();

        List<EntityData.CubeSegment> bodySegments = new ArrayList<>();

        EntityData.CubeSegment head = EntityData.CubeSegment.randomSquare(rand, MIN_BASE_BODY_SIZE, MAX_BASE_BODY_SIZE);
        EntityData.CubeSegment current = head;
        for (int i = 0; i < bodySegmentCount; i++) {
            current = nextSegment(current, bodyXSizeDistribution, bodyYZSizeDistribution);
            bodySegments.add(current);
        }

        Map<Integer, List<EntityData.CubeSegment>> decorations = new HashMap<>();
        // Body + Head
        List<EntityData.CubeSegment> allBodySegments = new ArrayList<>();
        allBodySegments.add(head);
        allBodySegments.addAll(bodySegments);
        generateFins(rand, allBodySegments, decorations);
        generateHeadFeatures(rand, head, decorations);
        if (!bodySegments.isEmpty()) {
            generateTailFeatures(rand, bodySegments.getLast(), bodySegments.size(), decorations);
        }

        return new EntityData(head, bodySegments, decorations);
    }

    private static EntityData.CubeSegment nextSegment(EntityData.CubeSegment current, Distribution<Integer> xDistribution,
                                                      Distribution<Integer> yZDistribution) {
        int dX = xDistribution.nextValue() - 3;
        int dY = yZDistribution.nextValue() - 3;
        int dZ = yZDistribution.nextValue() - 3;
        int x = Math.max(current.x() + dX, ABSOLUTE_MIN_BODY_SIZE);
        int y = Math.max(current.y() + dY, ABSOLUTE_MIN_BODY_SIZE);
        int z = Math.max(current.z() + dZ, ABSOLUTE_MIN_BODY_SIZE);

        return new EntityData.CubeSegment(current.x0() + current.x(),
                current.y0() + current.y() / 2.0F - y / 2.0F,
                current.z0() + current.z() / 2.0F - z / 2.0F,
                x, y, z);
    }

    private static RandomSource randomSource(Level level) {
        if (level != null) {
            return level.getRandom();
        }

        return RandomSource.create();
    }

    private static void generateFins(RandomSource rand, List<EntityData.CubeSegment> bodySegments, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        // Top fins
        if (rand.nextFloat() > 0.3) {
            float zRot = (rand.nextInt(20, 90)) * Mth.PI / 180.0F;
            for (int i = 0; i < bodySegments.size(); i++) {
                if (rand.nextFloat() > 0.1) {
                    EntityData.CubeSegment segment = bodySegments.get(i);
                    int y = rand.nextInt(1, segment.x() * 2 / 3 + 1);
                    int z = rand.nextInt(1, segment.z() / 3 + 1);
                    int x = rand.nextInt(Math.min(y, z), Math.max(y, z) + 1);
                    decorations.computeIfAbsent(i, ArrayList::new).add(new EntityData.CubeSegment(segment.x0() + segment.x() / 2.0F - x / 2.0F + Mth.sin(zRot) * x, segment.y0() - Mth.cos(zRot) * Mth.sqrt(y * y + z * z), segment.z0() + segment.z() / 2.0F - z / 2.0F, x, y, z, 0, 0, zRot, "top_fin"));
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
                    decorations.computeIfAbsent(i, ArrayList::new).add(new EntityData.CubeSegment(x0 + x, y0, segment.z0(), x, y, z, 0, Mth.PI, 0, "left_fin"));
                    decorations.computeIfAbsent(i, ArrayList::new).add(new EntityData.CubeSegment(x0, y0, segment.z0() + segment.z(), x, y, z, "right_fin"));
                }
            }
        }
    }

    private static void generateHeadFeatures(RandomSource rand, EntityData.CubeSegment head, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        if  (rand.nextFloat() > 0.3) {
            IntegerNormalDistribution noseXDistribution = new IntegerNormalDistribution(rand, 4, 3);
            IntegerNormalDistribution noseYZDistribution = new IntegerNormalDistribution(rand, 3, 4);
            // Nose
            int x = Mth.clamp(noseXDistribution.nextValue(), 1, head.x());
            int y = Mth.clamp(noseYZDistribution.nextValue(), 1, head.y() / 2);
            int z = Mth.clamp(noseYZDistribution.nextValue(), 1, head.z() / 2);
            float yOffset = rand.nextFloat() * (head.y() / 2.0F - y / 2.0F) + head.y() / 2.0F - y / 2.0F;
            float y0 = head.y0() + yOffset;

            decorations.computeIfAbsent(0, ArrayList::new).add(new EntityData.CubeSegment(head.x0() - x, y0, head.z0() + head.z() / 2.0F - z / 2.0F, x, y, z, "nose"));
        }
    }

    private static void generateTailFeatures(RandomSource rand, EntityData.CubeSegment lastBodySegment, int lastBodySegmentIndex, Map<Integer, List<EntityData.CubeSegment>> decorations) {
        if (rand.nextFloat() > 0.6) {
            // Flat tail
            IntegerNormalDistribution tailXDistribution = new IntegerNormalDistribution(rand, lastBodySegment.x(), lastBodySegment.x() / 2.0F);
            IntegerNormalDistribution tailYDistribution = new IntegerNormalDistribution(rand, 2, 1);
            IntegerNormalDistribution tailZDistribution = new IntegerNormalDistribution(rand, lastBodySegment.z(), lastBodySegment.z() / 2.0F);

            int x = Math.max(tailXDistribution.nextValue(), 3);
            int y = Mth.clamp(tailYDistribution.nextValue(), 1, lastBodySegment.y());
            int z = Mth.clamp(tailZDistribution.nextValue(), 1, lastBodySegment.z() * 2);

            decorations.computeIfAbsent(lastBodySegmentIndex, ArrayList::new).add(new EntityData.CubeSegment(
                    lastBodySegment.x0() + lastBodySegment.x(),
                    lastBodySegment.y0() + lastBodySegment.y() / 2.0F - y / 2.0F,
                    lastBodySegment.z0() + lastBodySegment.z() / 2.0F -  z / 2.0F,
                    x, y, z, "tail"));
        }

        // 2 Pronged Tail
        // TODO
    }
}
