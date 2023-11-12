package tao.dong.dataconjurer.common.support;

import lombok.Builder;

import java.util.concurrent.ThreadLocalRandom;

@Builder
public class RandomLongGenerator implements ValueGenerator<Long> {
    private static final ThreadLocalRandom RANDOM_GENERATOR = ThreadLocalRandom.current();

    @Builder.Default
    private long minInclusive = Long.MIN_VALUE;
    @Builder.Default
    private long maxExclusive = Long.MAX_VALUE;

    @Override
    public Long generate() {
        return RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive);
    }
}
