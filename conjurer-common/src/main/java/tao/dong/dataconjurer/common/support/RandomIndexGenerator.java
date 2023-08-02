package tao.dong.dataconjurer.common.support;

import java.util.random.RandomGenerator;

public class RandomIndexGenerator implements IndexValueGenerator {

    private final int size;
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();


    public RandomIndexGenerator(int size) {
        this.size = size;

        if (size < 1) {
            throw new IllegalArgumentException("Invalid size for random index generator. size: " + size);
        }
    }

    @Override
    public Integer generate() {
        return randomGenerator.nextInt(size);
    }
}
