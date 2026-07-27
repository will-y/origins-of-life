package dev.willyelton.origins.common.entity;

import net.minecraft.util.RandomSource;

import java.util.List;

/// Stores all attributes of an entity (cannot change)
public record EntityData(List<CubeSegment> bodySegments) {
    public record CubeSegment(int x, int y, int z) {
        public static CubeSegment random(RandomSource random, int min, int max) {
            int x = random.nextIntBetweenInclusive(min, max);
            int y = random.nextIntBetweenInclusive(min, max);
            int z = random.nextIntBetweenInclusive(min, max);

            return new CubeSegment(x, y, z);
        }

        public static CubeSegment randomSquare(RandomSource random, int min, int max) {
            int i = random.nextIntBetweenInclusive(min, max);
            return new CubeSegment(i, i, i);
        }
    }
}
