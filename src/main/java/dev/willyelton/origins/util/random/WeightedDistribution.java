package dev.willyelton.origins.util.random;

import com.google.j2objc.annotations.ObjectiveCAdapterMethod;
import net.minecraft.util.RandomSource;

import java.util.Arrays;

/// Generates a random integer based off of the provided weights.
///
/// The weights are the chance of that index happening.
///
/// Ex: [0, 50, 50] would be 0% 0, 50% 1 and 50% 2.
public class WeightedDistribution implements Distribution<Integer> {
    private final RandomSource random;
    private final int[] weights;
    private final int totalWeights;


    public WeightedDistribution(RandomSource random, int[] weights) {
        this.random = random;
        this.weights = weights;
        this.totalWeights = Arrays.stream(weights).sum();
    }

    @Override
    public Integer nextValue() {
        int r = random.nextInt(totalWeights);
        int currentSum = 0;

        for (int i = 0; i < weights.length; i++) {
            currentSum += weights[i];
            if (r < currentSum) {
                return i;
            }
        }

        return 1;
    }
}
