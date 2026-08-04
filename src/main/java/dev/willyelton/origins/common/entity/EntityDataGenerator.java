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
    private static final int[] BODY_COUNT_WEIGHTS = new int[]{0, 75, 20, 10, 5, 4, 3, 2, 1, 1, 1};
    // -3
    private static final int[] BODY_NEXT_X_SIZE_WEIGHTS = new int[]{60, 70, 80, 90, 10};
    // -3
    private static final int[] BODY_NEXT_Y_Z_SIZE_WEIGHTS = new int[]{60, 70, 80, 90, 10};

    public static EntityData empty() {
        return new EntityData(new ArrayList<>());
    }

    public static EntityData random() {
        RandomSource rand = randomSource();
        Distribution bodyCountDistribution = new WeightedDistribution(rand, BODY_COUNT_WEIGHTS);
        Distribution bodyXSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_X_SIZE_WEIGHTS);
        Distribution bodyYZSizeDistribution = new WeightedDistribution(rand, BODY_NEXT_Y_Z_SIZE_WEIGHTS);

        int bodySegmentCount = bodyCountDistribution.nextValue();

        List<EntityData.CubeSegment> bodySegments = new ArrayList<>();

        // TODO: Base this off of the head
        EntityData.CubeSegment current = EntityData.CubeSegment.randomSquare(rand, MIN_BASE_BODY_SIZE, MAX_BASE_BODY_SIZE);
        bodySegments.add(current);
        for (int i = 1; i < bodySegmentCount; i++) {
            current = nextSegment(current, bodyXSizeDistribution, bodyYZSizeDistribution);
            bodySegments.add(current);
        }

        return new EntityData(bodySegments);
    }

    private static EntityData.CubeSegment nextSegment(EntityData.CubeSegment current, Distribution xDistribution, Distribution yZDistribution) {
        int dX = xDistribution.nextValue() - 3;
        int dY = yZDistribution.nextValue() - 3;
        int dZ = yZDistribution.nextValue() - 3;

        return new EntityData.CubeSegment(Math.max(current.x() + dX, ABSOLUTE_MIN_BODY_SIZE),
                Math.max(current.y() + dY, ABSOLUTE_MIN_BODY_SIZE),
                Math.max(current.z() + dZ, ABSOLUTE_MIN_BODY_SIZE));
    }

    private static RandomSource randomSource() {
        Level level = Minecraft.getInstance().level;

        if (level != null) {
            return level.getRandom();
        }

        return RandomSource.create();
    }
}
