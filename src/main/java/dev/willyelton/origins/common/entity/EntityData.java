package dev.willyelton.origins.common.entity;

import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/// Stores all attributes of an entity (cannot change)
public final class EntityData {
    private final List<CubeSegment> bodySegments;
    private @Nullable Sizes sizes = null;

    public EntityData(List<CubeSegment> bodySegments) {
        this.bodySegments = bodySegments;
    }

    public List<CubeSegment> bodySegments() {
        return bodySegments;
    }

    /// Returns some things about the computed model's size (in model space, not block space)
    public Sizes sizes() {
        if (sizes == null) {
            int maxX = bodySegments().stream().mapToInt(EntityData.CubeSegment::x).sum();
            int maxY = bodySegments().stream().mapToInt(EntityData.CubeSegment::y).max().orElse(0);
            int maxZ = bodySegments().stream().mapToInt(EntityData.CubeSegment::z).max().orElse(0);
            sizes = new Sizes(maxX, maxY, maxZ, maxX / 2.0F, maxY / 2.0F, maxZ / 2.0F);
        }
        return sizes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EntityData) obj;
        return Objects.equals(this.bodySegments, that.bodySegments) &&
                Objects.equals(this.sizes, that.sizes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bodySegments, sizes);
    }

    @Override
    public String toString() {
        return "EntityData[" +
                "bodySegments=" + bodySegments + ", " +
                "sizes=" + sizes + ']';
    }

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

    public record Sizes(int maxX, int maxY, int maxZ, float centerX, float centerY, float centerZ) {
    }
}
