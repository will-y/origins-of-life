package dev.willyelton.origins.common.entity.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.origins.common.entity.data.behavior.Behavior;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/// Stores all attributes of an entity (cannot change)
public final class EntityData {
    public static final Codec<Integer> STRINT = Codec.STRING.xmap(Integer::parseInt, String::valueOf);

    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModelData.CODEC.fieldOf("modelData").forGetter(EntityData::modelData),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("animationData").forGetter(EntityData::animationData),
            Codec.unboundedMap(Attribute.CODEC, Codec.DOUBLE).fieldOf("defaultAttributes").forGetter(EntityData::defaultAttributes),
            Codec.INT.fieldOf("color").forGetter(EntityData::color),
            Codec.INT.fieldOf("eyeColor").forGetter(EntityData::eyeColor),
            Behavior.CODEC.listOf().fieldOf("behaviors").forGetter(EntityData::behaviors),
            TagKey.codec(Registries.ITEM).optionalFieldOf("foodTag").xmap(k -> k.orElse(ItemTags.COW_FOOD), Optional::ofNullable).forGetter(EntityData::foodTag)
    ).apply(instance, EntityData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityData> STREAM_CODEC = StreamCodec.composite(
            ModelData.STREAM_CODEC, EntityData::modelData,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT), EntityData::animationData,
            ByteBufCodecs.map(HashMap::new, Attribute.STREAM_CODEC, ByteBufCodecs.DOUBLE), EntityData::defaultAttributes,
            ByteBufCodecs.INT, EntityData::color,
            ByteBufCodecs.INT, EntityData::eyeColor,
            Behavior.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityData::behaviors,
            ByteBufCodecs.optional(TagKey.streamCodec(Registries.ITEM)).map(k -> k.orElse(ItemTags.COW_FOOD), Optional::ofNullable), EntityData::foodTag,
            EntityData::new);

    private final ModelData modelData;
    private final Map<String, Float> animationData;
    private final Map<Holder<Attribute>, Double> defaultAttributes;
    // TODO: Might move to cube segment eventually? Or move to something else idk
    private final int color;
    private final int eyeColor;
    /// List of behaviors in priority order. These will create goals and target goals for the entity
    private final List<Behavior> behaviors;
    private final TagKey<Item> foodTag;

    public EntityData(CubeSegment head, List<CubeSegment> bodySegments, Map<Integer, List<CubeSegment>> decorations,
                      Map<String, Float> animationData, Map<Holder<Attribute>, Double> defaultAttributes, int color,
                      int eyeColor, List<Behavior> behaviors, TagKey<Item> foodTag) {
        this(new ModelData(head, bodySegments, decorations), animationData, defaultAttributes, color, eyeColor, behaviors, foodTag);
    }

    public EntityData(ModelData modelData, Map<String, Float> animationData, Map<Holder<Attribute>, Double> defaultAttributes,
                      int color, int eyeColor, List<Behavior> behaviors, TagKey<Item> foodTag) {
        this.modelData = modelData;
        this.animationData = animationData;
        this.defaultAttributes = defaultAttributes;
        this.color = color;
        this.eyeColor = eyeColor;
        this.behaviors = behaviors;
        this.foodTag = foodTag;
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

    public int eyeColor() {
        return eyeColor;
    }

    public List<Behavior> behaviors() {
        return behaviors;
    }

    public List<CubeSegment> allSegments() {
        return modelData.allSegments();
    }

    public TagKey<Item> foodTag() {
        return this.foodTag;
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

    public List<Component> displayComponents(LivingEntity entity) {
        List<CubeSegment> decorations = this.modelData.decorations.values().stream().flatMap(List::stream).toList();
        Style headerStyle = Style.EMPTY.withBold(true).withColor(TextColor.GRAY);

        List<Component> results = new ArrayList<>();
        results.add(entity.getDisplayName());
        results.add(Component.literal("-----------------"));
        results.add(Component.literal("Model").withStyle(headerStyle));
        results.add(Component.literal(String.format("    %s Body Segments", this.modelData.bodySegments.size())));
        results.add(Component.literal(String.format("    %s Fins", decorationsWithName("fin", decorations))));
        if (decorationsWithName("tail", decorations) > 0) {
            results.add(Component.literal("        Tail"));
        }
        int eyeCount = decorationsWithName("eye", decorations);
        if (eyeCount == 1) {
            results.add(Component.literal("    1 Eye"));
        } else if (eyeCount == 2) {
            results.add(Component.literal("    2 Eyes"));
        }

        results.add(Component.literal("Colors").withStyle(headerStyle));
        results.add(Component.literal(String.format("    Body Color: %d", this.color)).withColor(this.color));
        results.add(Component.literal(String.format("    Eye Color: %d", this.eyeColor)).withColor(this.eyeColor));

        results.add(Component.literal("Attributes").withStyle(headerStyle));
        defaultAttributes.forEach((key, v) -> {
            results.add(Component.literal("    ").append(key.value().toBaseComponent(v, entity.getAttributeBaseValue(key), false, TooltipFlag.NORMAL)));
        });

        results.add(Component.literal("Behaviors").withStyle(headerStyle));
        behaviors.forEach(behavior -> {
            results.add(Component.literal(String.format("    %s", behavior.description())).withColor(behavior.displayColor()));
        });

        results.add(Component.literal("Breeding Items").withStyle(headerStyle));
        int count = 0;
        boolean broken = false;
        for (Holder<Item> itemHolder : BuiltInRegistries.ITEM.getTagOrEmpty(this.foodTag)) {
            if (++count > 10) {
                broken = true;
            } else if (!broken) {
                results.add(itemHolder.value().getName(new ItemStack(itemHolder.value())));
            }
        }

        if (broken) {
            results.add(Component.literal(String.format("and %s more", count - 10)));
        }

        return results;
    }

    private int decorationsWithName(String name, List<CubeSegment> decorations) {
        return (int) decorations.stream().filter(segment -> segment.name.contains(name)).count();
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
