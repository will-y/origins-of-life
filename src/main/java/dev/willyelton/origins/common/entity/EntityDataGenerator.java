package dev.willyelton.origins.common.entity;

import dev.willyelton.origins.util.random.Distribution;
import dev.willyelton.origins.util.random.WeightedDistribution;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Distribution bodyCountDistribution = new WeightedDistribution(rand, BODY_COUNT_WEIGHTS);
        Distribution bodyXSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_X_SIZE_WEIGHTS);
        Distribution bodyYZSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_Y_Z_SIZE_WEIGHTS);

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

        return new EntityData(head, bodySegments, decorations);
    }

    private static EntityData.CubeSegment nextSegment(EntityData.CubeSegment current, Distribution xDistribution,
                                                      Distribution yZDistribution) {
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
                EntityData.CubeSegment segment = bodySegments.get(i);
                int y = rand.nextInt(1, segment.x() * 2 / 3 + 1);
                int z = rand.nextInt(1, segment.z() / 3 + 1);
                int x = rand.nextInt(Math.min(y, z), Math.max(y, z) + 1);
                decorations.put(i, List.of(new EntityData.CubeSegment(segment.x0() + segment.x() / 2.0F - x / 2.0F + Mth.sin(zRot) * x, segment.y0() - Mth.cos(zRot) * Mth.sqrt(y * y + z * z), segment.z0() + segment.z() / 2.0F - z / 2.0F, x, y, z, 0, 0, zRot, "top_fin")));
            }
        }
    }
}
