package tao.dong.dataconjurer.common.support;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;

@Builder
@Getter
public class RandomNumberGenerator implements ValueGenerator<BigDecimal> {

    private static final Random RANDOM_GENERATOR = new SecureRandom();

    @Builder.Default
    private long maxExclusive = Long.MAX_VALUE;
    @Builder.Default
    private long minInclusive = Long.MIN_VALUE;
    @Builder.Default
    private int precision = 0;

    @Override
    public BigDecimal generate() {
        var strVal = isDoubleType() ?
                String.valueOf(RANDOM_GENERATOR.nextLong(minInclusive + 1, maxExclusive)) + '.' + RANDOM_GENERATOR.nextInt((int)Math.pow(10D, precision)) :
                String.valueOf(RANDOM_GENERATOR.nextLong(minInclusive, maxExclusive));
        return new BigDecimal(strVal);
    }

    public boolean isDoubleType() {
        return precision > 0;
    }
}
