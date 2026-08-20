package dev.willyelton.origins.common.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/// Stores all attributes of an entity (cannot change)
public final class EntityData {
    public static final Codec<Integer> STRINT = Codec.STRING.xmap(Integer::parseInt, String::valueOf);

    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CubeSegment.CODEC.fieldOf("head").forGetter(EntityData::head),
            CubeSegment.CODEC.listOf().fieldOf("bodySegments").forGetter(EntityData::bodySegments),
            Codec.unboundedMap(STRINT, CubeSegment.CODEC.listOf()).fieldOf("decorations").forGetter(EntityData::decorations),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("animationData").forGetter(EntityData::animationData),
            Codec.unboundedMap(Attribute.CODEC, Codec.DOUBLE).fieldOf("defaultAttributes").forGetter(EntityData::defaultAttributes)
    ).apply(instance, EntityData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
            CubeSegment.STREAM_CODEC, EntityData::head,
            CubeSegment.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityData::bodySegments,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, CubeSegment.STREAM_CODEC.apply(ByteBufCodecs.list())), EntityData::decorations,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT), EntityData::animationData,
            ByteBufCodecs.map(HashMap::new, Attribute.STREAM_CODEC, ByteBufCodecs.DOUBLE), EntityData::defaultAttributes,
            EntityData::new);

    private final CubeSegment head;
    private final List<CubeSegment> bodySegments;
    private final Map<Integer, List<CubeSegment>> decorations;
    private @Nullable List<CubeSegment> allSegments;
    private final Map<String, Float> animationData;
    private final Map<Holder<Attribute>, Double> defaultAttributes;

    private @Nullable Sizes sizes = null;

    public EntityData(CubeSegment head, List<CubeSegment> bodySegments, Map<Integer, List<CubeSegment>> decorations,
                      Map<String, Float> animationData, Map<Holder<Attribute>, Double> defaultAttributes) {
        this.head = head;
        this.bodySegments = bodySegments;
        this.decorations = decorations;
        this.animationData = animationData;
        this.defaultAttributes = defaultAttributes;
    }

    public CubeSegment head() {
        return head;
    }

    public List<CubeSegment> bodySegments() {
        return bodySegments;
    }

    public Map<Integer, List<CubeSegment>> decorations() {
        return decorations;
    }

    public Map<String, Float> animationData() {
        return animationData;
    }

    public Map<Holder<Attribute>, Double> defaultAttributes() {
        return defaultAttributes;
    }

    public List<CubeSegment> allSegments() {
        if (allSegments == null) {
            allSegments = Stream.concat(Stream.of(head), bodySegments.stream()).toList();
        }

        return allSegments;
    }

    /// Returns some things about the computed model's size (in model space, not block space)
    /// TODO: Fix these now that we know placements
    public Sizes sizes() {
        if (sizes == null) {
            int maxX = allSegments().stream().mapToInt(EntityData.CubeSegment::x).sum();
            int maxY = allSegments().stream().mapToInt(EntityData.CubeSegment::y).max().orElse(0);
            int maxZ = allSegments().stream().mapToInt(EntityData.CubeSegment::z).max().orElse(0);
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

    // Add uv here
    public record CubeSegment(float x0, float y0, float z0, int x, int y, int z, float xRot, float yRot, float zRot, String name) {
        public static final Codec<CubeSegment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("x0").forGetter(CubeSegment::x0),
                Codec.FLOAT.fieldOf("y0").forGetter(CubeSegment::y0),
                Codec.FLOAT.fieldOf("z0").forGetter(CubeSegment::z0),
                Codec.INT.fieldOf("x").forGetter(CubeSegment::x),
                Codec.INT.fieldOf("y").forGetter(CubeSegment::y),
                Codec.INT.fieldOf("z").forGetter(CubeSegment::z),
                Codec.FLOAT.fieldOf("xRot").forGetter(CubeSegment::xRot),
                Codec.FLOAT.fieldOf("yRot").forGetter(CubeSegment::yRot),
                Codec.FLOAT.fieldOf("zRot").forGetter(CubeSegment::zRot),
                Codec.STRING.fieldOf("name").forGetter(CubeSegment::name)
        ).apply(instance, CubeSegment::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CubeSegment> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, CubeSegment::x0,
                ByteBufCodecs.FLOAT, CubeSegment::y0,
                ByteBufCodecs.FLOAT, CubeSegment::z0,
                ByteBufCodecs.INT, CubeSegment::x,
                ByteBufCodecs.INT, CubeSegment::y,
                ByteBufCodecs.INT, CubeSegment::z,
                ByteBufCodecs.FLOAT, CubeSegment::xRot,
                ByteBufCodecs.FLOAT, CubeSegment::yRot,
                ByteBufCodecs.FLOAT, CubeSegment::zRot,
                ByteBufCodecs.STRING_UTF8, CubeSegment::name,
                CubeSegment::new);

        public CubeSegment(int x, int y, int z) {
            this(0, 0, 0, x, y, z);
        }

        public CubeSegment(float x0, float y0, float z0, int x, int y, int z) {
            this(x0, y0, z0, x, y, z, 0, 0, 0, "");
        }

        public CubeSegment(float x0, float y0, float z0, int x, int y, int z, String name) {
            this(x0, y0, z0, x, y, z, 0, 0, 0, name);
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
