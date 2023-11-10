package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

@Builder
@Getter
public class RandomNumberGenerator implements ValueGenerator<BigDecimal> {

    private static final ThreadLocalRandom RANDOM_GENERATOR = ThreadLocalRandom.current();

    @Builder.Default
    private long maxExclusive = Long.MAX_VALUE;
    @Builder.Default
    private long minInclusive = Long.MIN_VALUE;
    @Builder.Default
    private int precision = 0;

    @Override
    public BigDecimal generate() {
        var strVal = isDoubleType() ?
                String.valueOf(RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive)) + '.' + RANDOM_GENERATOR.nextInt((int)Math.pow(10D, precision)) :
                String.valueOf(RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive));
        return new BigDecimal(strVal);
    }

    public boolean isDoubleType() {
        return precision > 0;
    }
}
