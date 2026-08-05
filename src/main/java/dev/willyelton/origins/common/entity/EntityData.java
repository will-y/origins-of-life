package dev.willyelton.origins.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/// Stores all attributes of an entity (cannot change)
public final class EntityData {
    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CubeSegment.CODEC.listOf().fieldOf("bodySegments").forGetter(EntityData::bodySegments)
    ).apply(instance, EntityData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
            CubeSegment.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityData::bodySegments,
            EntityData::new);

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

    public record CubeSegment(float x0, float y0, float z0, int x, int y, int z) {
        public static final Codec<CubeSegment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("x0").forGetter(CubeSegment::x0),
                Codec.FLOAT.fieldOf("y0").forGetter(CubeSegment::y0),
                Codec.FLOAT.fieldOf("z0").forGetter(CubeSegment::z0),
                Codec.INT.fieldOf("x").forGetter(CubeSegment::x),
                Codec.INT.fieldOf("y").forGetter(CubeSegment::y),
                Codec.INT.fieldOf("z").forGetter(CubeSegment::z)
        ).apply(instance, CubeSegment::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CubeSegment> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, CubeSegment::x0,
                ByteBufCodecs.FLOAT, CubeSegment::y0,
                ByteBufCodecs.FLOAT, CubeSegment::z0,
                ByteBufCodecs.INT, CubeSegment::x,
                ByteBufCodecs.INT, CubeSegment::y,
                ByteBufCodecs.INT, CubeSegment::z,
                CubeSegment::new);

        public CubeSegment(int x, int y, int z) {
            this(0, 0, 0, x, y, z);
        }

        public CubeSegment withOffset(float x0, float y0, float z0) {
            return new CubeSegment(x0, y0, z0, x, y, z);
        }

        public static CubeSegment randomSquare(RandomSource random, int min, int max) {
            int i = random.nextIntBetweenInclusive(min, max);
            return new CubeSegment(i, i, i);
        }
    }

    public record Sizes(int maxX, int maxY, int maxZ, float centerX, float centerY, float centerZ) {
    }
}
