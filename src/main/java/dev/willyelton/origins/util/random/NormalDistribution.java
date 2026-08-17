package dev.willyelton.origins.util.random;

import net.minecraft.util.RandomSource;

/// Normal distribution (approximated using Irwin-Hall)
public class NormalDistribution implements Distribution<Float> {

    private final RandomSource rand;
    private final float mean;
    private final float variance;

    public NormalDistribution(RandomSource rand, float mean, float variance) {
        this.rand = rand;
        this.mean = mean;
        this.variance = variance;
    }

    @Override
    public Float nextValue() {
        float sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += rand.nextFloat();
        }

        sum -= 6;
        return sum * variance + mean;
    }
}
