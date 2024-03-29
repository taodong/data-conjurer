package tao.dong.dataconjurer.common.support;

import java.util.random.RandomGenerator;

public class RandomIndexGenerator implements IndexValueGenerator {

    private static final RandomGenerator RANDOM_GENERATOR = RandomGenerator.getDefault();
    private final int size;

    public RandomIndexGenerator(int size) {
        this.size = size;

        if (size < 1) {
            throw new IllegalArgumentException("Invalid size for random index generator. size: " + size);
        }
    }

    @Override
    public Integer generate() {
        return RANDOM_GENERATOR.nextInt(size);
    }
}
