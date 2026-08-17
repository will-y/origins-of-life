package dev.willyelton.origins.util.random;

import net.minecraft.util.RandomSource;

/// Rounded integer value from a {@link NormalDistribution}
public class IntegerNormalDistribution implements Distribution<Integer> {

    private final NormalDistribution delegate;

    public IntegerNormalDistribution(RandomSource rand, float mean, float variance) {
        this.delegate = new NormalDistribution(rand, mean, variance);
    }

    @Override
    public Integer nextValue() {
        return Math.round(delegate.nextValue());
    }
}
