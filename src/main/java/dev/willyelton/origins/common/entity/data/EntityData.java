package dev.willyelton.origins.common.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/// Stores all attributes of an entity (cannot change)
public final class EntityData {
    public static final Codec<Integer> STRINT = Codec.STRING.xmap(Integer::parseInt, String::valueOf);

    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModelData.CODEC.fieldOf("modelData").forGetter(EntityData::modelData),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("animationData").forGetter(EntityData::animationData),
            Codec.unboundedMap(Attribute.CODEC, Codec.DOUBLE).fieldOf("defaultAttributes").forGetter(EntityData::defaultAttributes),
            Codec.INT.fieldOf("color").forGetter(EntityData::color),
            Behavior.CODEC.listOf().fieldOf("behaviors").forGetter(EntityData::behaviors)
    ).apply(instance, EntityData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
            ModelData.STREAM_CODEC, EntityData::modelData,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT), EntityData::animationData,
            ByteBufCodecs.map(HashMap::new, Attribute.STREAM_CODEC, ByteBufCodecs.DOUBLE), EntityData::defaultAttributes,
            ByteBufCodecs.INT, EntityData::color,
            Behavior.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityData::behaviors,
            EntityData::new);

    private final ModelData modelData;
    private final Map<String, Float> animationData;
    private final Map<Holder<Attribute>, Double> defaultAttributes;
    // TODO: Might move to cube segment eventually? Or move to something else idk
    private final int color;
    /// List of behaviors in priority order. These will create goals and target goals for the entity
    private final List<Behavior> behaviors;

    public EntityData(CubeSegment head, List<CubeSegment> bodySegments, Map<Integer, List<CubeSegment>> decorations,
                      Map<String, Float> animationData, Map<Holder<Attribute>, Double> defaultAttributes, int color,
                      List<Behavior> behaviors) {
        this(new ModelData(head, bodySegments, decorations), animationData, defaultAttributes, color, behaviors);
    }

    public EntityData(ModelData modelData, Map<String, Float> animationData, Map<Holder<Attribute>, Double> defaultAttributes,
                      int color, List<Behavior> behaviors) {
        this.modelData = modelData;
        this.animationData = animationData;
        this.defaultAttributes = defaultAttributes;
        this.color = color;
        this.behaviors = behaviors;
    }

    public ModelData modelData() {
        return modelData;
    }

    public Map<String, Float> animationData() {
        return animationData;
    }

    public Map<Holder<Attribute>, Double> defaultAttributes() {
        return defaultAttributes;
    }

    public int color() {
        return color;
    }

    public List<Behavior> behaviors() {
        return behaviors;
    }

    public List<CubeSegment> allSegments() {
        return modelData.allSegments();
    }

    /// Returns some things about the computed model's size (in model space, not block space)
    public Sizes sizes() {
        return modelData.sizes();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EntityData that = (EntityData) o;
        return color == that.color && Objects.equals(modelData, that.modelData) && Objects.equals(animationData, that.animationData) && Objects.equals(defaultAttributes, that.defaultAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelData, animationData, defaultAttributes, color);
    }

    @Override
    public String toString() {
        return "EntityData{" +
                "modelData=" + modelData +
                ", animationData=" + animationData +
                ", defaultAttributes=" + defaultAttributes +
                ", color=" + color +
                '}';
    }

    public record CubeSegment(float x0, float y0, float z0, int x, int y, int z, float xRot, float yRot, float zRot,
                              int u, int v, String name) {
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
                Codec.INT.fieldOf("u").forGetter(CubeSegment::u),
                Codec.INT.fieldOf("v").forGetter(CubeSegment::v),
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
                ByteBufCodecs.INT, CubeSegment::u,
                ByteBufCodecs.INT, CubeSegment::v,
                ByteBufCodecs.STRING_UTF8, CubeSegment::name,
                CubeSegment::new);

        public CubeSegment(int x, int y, int z, int u, int v) {
            this(0, 0, 0, x, y, z, u, v);
        }

        public CubeSegment(float x0, float y0, float z0, int x, int y, int z, int u, int v) {
            this(x0, y0, z0, x, y, z, 0, 0, 0, u, v, "");
        }

        public CubeSegment(float x0, float y0, float z0, int x, int y, int z, int u, int v, String name) {
            this(x0, y0, z0, x, y, z, 0, 0, 0, u, v, name);
        }

        public CubeSegment withOffset(float x0, float y0, float z0, int u, int v) {
            return new CubeSegment(x0, y0, z0, x, y, z, u, v);
        }

        public static CubeSegment randomSquare(RandomSource random, int min, int max, int u, int v) {
            int i = random.nextIntBetweenInclusive(min, max);
            return new CubeSegment(i, i, i, u, v);
        }
    }

    public record ModelData(CubeSegment head, List<CubeSegment> bodySegments,
                            Map<Integer, List<CubeSegment>> decorations, List<CubeSegment> allSegments, Sizes sizes) {
        public static final Codec<ModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CubeSegment.CODEC.fieldOf("head").forGetter(ModelData::head),
                CubeSegment.CODEC.listOf().fieldOf("bodySegments").forGetter(ModelData::bodySegments),
                Codec.unboundedMap(STRINT, CubeSegment.CODEC.listOf()).fieldOf("decorations").forGetter(ModelData::decorations)
        ).apply(instance, ModelData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ModelData> STREAM_CODEC = StreamCodec.composite(
                CubeSegment.STREAM_CODEC, ModelData::head,
                CubeSegment.STREAM_CODEC.apply(ByteBufCodecs.list()), ModelData::bodySegments,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.INT, CubeSegment.STREAM_CODEC.apply(ByteBufCodecs.list())), ModelData::decorations,
                ModelData::new);

        public ModelData(CubeSegment head, List<CubeSegment> bodySegments,
                         Map<Integer, List<CubeSegment>> decorations) {
            List<CubeSegment> allSegments = Stream.concat(Stream.of(head), bodySegments.stream()).toList();
            int maxX = allSegments.stream().mapToInt(EntityData.CubeSegment::x).sum();
            int maxY = allSegments.stream().mapToInt(EntityData.CubeSegment::y).max().orElse(0);
            int maxZ = allSegments.stream().mapToInt(EntityData.CubeSegment::z).max().orElse(0);
            Sizes sizes = new Sizes(maxX, maxY, maxZ, maxX / 2.0F, maxY / 2.0F, maxZ / 2.0F);

            this(head, bodySegments, decorations, allSegments, sizes);
        }
    }

    public record Sizes(int maxX, int maxY, int maxZ, float centerX, float centerY, float centerZ) {
    }
}
