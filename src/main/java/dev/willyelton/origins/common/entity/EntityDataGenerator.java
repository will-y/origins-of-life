package dev.willyelton.origins.common.entity;

import dev.willyelton.origins.util.random.Distribution;
import dev.willyelton.origins.util.random.WeightedDistribution;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EntityDataGenerator {
    private static final int MAX_BASE_BODY_SIZE = 25;
    private static final int MIN_BASE_BODY_SIZE = 5;
    private static final int ABSOLUTE_MIN_BODY_SIZE = 3;
    private static final int[] BODY_COUNT_WEIGHTS = new int[] {0, 75, 20, 10, 5, 4, 3, 2, 1, 1, 1};
    private static final int[] BODY_SIZE_STEP_DISTRIBUTION = new int[] {90, 75, 40, 5};

    public static EntityData empty() {
        return new EntityData(new ArrayList<>());
    }

    public static EntityData random() {
        RandomSource rand = randomSource();
        Distribution bodyCountDistribution = new WeightedDistribution(rand, BODY_COUNT_WEIGHTS);
        Distribution bodySizeStepDistribution = new WeightedDistribution(rand, BODY_SIZE_STEP_DISTRIBUTION);

        int bodySegmentCount = bodyCountDistribution.nextValue();

        List<EntityData.CubeSegment> bodySegments = new ArrayList<>();

        EntityData.CubeSegment current = EntityData.CubeSegment.randomSquare(rand, MIN_BASE_BODY_SIZE, MAX_BASE_BODY_SIZE);
        bodySegments.add(current);
        for (int i = 1; i < bodySegmentCount; i++) {
            int newSize = addRange(bodySizeStepDistribution, current.x());
            current = new EntityData.CubeSegment(newSize, newSize, newSize);
            bodySegments.add(current);

        }

        return new EntityData(bodySegments);
    }

    private static int addRange(Distribution dist, int value) {
        return Math.max(dist.nextValue() - 2 + value, ABSOLUTE_MIN_BODY_SIZE);
    }

    private static RandomSource randomSource() {
        Level level = Minecraft.getInstance().level;

        if (level != null) {
            return level.getRandom();
        }

        return RandomSource.create();
    }
}
