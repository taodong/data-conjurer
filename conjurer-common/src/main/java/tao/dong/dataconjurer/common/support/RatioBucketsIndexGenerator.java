package tao.dong.dataconjurer.common.support;

import jakarta.annotation.Nonnull;
import tao.dong.dataconjurer.common.model.RatioRange;

import java.util.random.RandomGenerator;

public class RatioBucketsIndexGenerator implements IndexValueGenerator{

    private static final int defaultIndex = -1;
    private static final RandomGenerator randomGenerator = RandomGenerator.getDefault();
    private final RatioRange[] buckets;

    public RatioBucketsIndexGenerator(@Nonnull RatioRange[] buckets) {
        this.buckets = buckets;
    }

    @Override
    public Integer generate() {
        return null;
    }
}
