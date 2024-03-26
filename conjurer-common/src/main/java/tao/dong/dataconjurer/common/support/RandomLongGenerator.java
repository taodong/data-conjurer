package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.Random;

@Builder
@Getter(AccessLevel.PACKAGE)
public class RandomLongGenerator implements ValueGenerator<Long> {
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    @Builder.Default
    private long minInclusive = Long.MIN_VALUE;
    @Builder.Default
    private long maxExclusive = Long.MAX_VALUE;

    @Override
    public Long generate() {
        return RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive);
    }
}
