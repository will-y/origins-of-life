package dev.willyelton.origins.common.entity.data;

/// Just a list of animation data strings and what they do
public class AnimationData {
    private AnimationData() {}

    /// Determines if the bodies on {@link dev.willyelton.origins.common.entity.AquaticCreature}s animate vertical (val > 0) or horizontal (val <= 0)
    public static final String AQUATIC_DIRECTION = "AQUATIC_DIRECTION";

    /// Determines how fast an {@link dev.willyelton.origins.common.entity.AquaticCreature}'s body rotates (default is 1)
    public static final String AQUATIC_BASE_ANGLE_MULTIPLIER = "AQUATIC_BASE_ANGLE_MULTIPLIER";

    /// Determines how far an {@link dev.willyelton.origins.common.entity.AquaticCreature}'s body rotates (default is 1)
    public static final String AQUATIC_BASE_AMPLITUDE_MULTIPLIER = "AQUATIC_BASE_AMPLITUDE_MULTIPLIER";
}
