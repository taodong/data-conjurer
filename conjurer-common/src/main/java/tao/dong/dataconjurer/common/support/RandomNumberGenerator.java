package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;

@Builder
public class RandomNumberGenerator implements ValueGenerator<BigDecimal> {

    private static final RandomGenerator RANDOM_GENERATOR = RandomGenerator.getDefault();

    @Builder.Default
    private long maxExclusive = Long.MAX_VALUE;
    @Builder.Default
    private long minInclusive = Long.MIN_VALUE;
    @Builder.Default
    @Getter
    private int precision = 0;

    @Override
    public BigDecimal generate() {
        return isDoubleType() ? new BigDecimal(
                RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive) + '.' + RANDOM_GENERATOR.nextInt(10 * precision)
        ) : new BigDecimal(RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive));
    }

    public boolean isDoubleType() {
        return precision > 0;
    }
}
