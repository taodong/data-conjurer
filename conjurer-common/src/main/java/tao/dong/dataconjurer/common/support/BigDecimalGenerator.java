package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.ChainedValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.NumberRange;
import tao.dong.dataconjurer.common.model.Precision;

import java.math.BigDecimal;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CHAIN;
import static tao.dong.dataconjurer.common.model.ConstraintType.NUMBER_RANGE;
import static tao.dong.dataconjurer.common.model.ConstraintType.PRECISION;


public class BigDecimalGenerator extends ValueGeneratorDecorator<BigDecimal>{
    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(NUMBER_RANGE, PRECISION, CHAIN);

    public BigDecimalGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected ValueGenerator<BigDecimal> getDefaultGenerator() {
        return createRandomNumberGenerator(Long.MIN_VALUE, Long.MAX_VALUE, 0);
    }

    @Override
    protected ValueGenerator<BigDecimal> createGenerator(Set<Constraint<?>> constraints) {
        long min = Long.MIN_VALUE;
        long max = Long.MAX_VALUE;
        int precision = 0;
        ChainedValue chain = null;
        for (var constraint : constraints) {
            if (NUMBER_RANGE == constraint.getType()) {
                var range = (NumberRange) constraint;
                max = Math.min(range.getMax(), max);
                min = Math.max(range.getMin(), min);
            } else if (PRECISION == constraint.getType()) {
                precision = Math.max(((Precision)constraint).getMax(), precision);
            } else if (CHAIN == constraint.getType()) {
                chain = (ChainedValue) constraint;
            }
        }

        if (chain != null) {
            return switch (Integer.compare(0, chain.getDirection())) {
                case 1 -> new ChainedBigDecimalGenerator(1, chain.getSeed(), chain.getStyle(), createRandomNumberGenerator(min, min + Double.valueOf(2 * chain.getSeed()).longValue(), precision).generate());
                case -1 -> new ChainedBigDecimalGenerator(-1, chain.getSeed(), chain.getStyle(), createRandomNumberGenerator(max - Double.valueOf(2 * chain.getSeed()).longValue(), max, precision).generate());
                default -> new ChainedBigDecimalGenerator(0, chain.getSeed(), chain.getStyle(), createRandomNumberGenerator(min, max, precision).generate());
            };
        } else {
            return createRandomNumberGenerator(min, max, precision);
        }
    }

    @Override
    protected Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints) {
        return FILTER_CONSTRAINTS.apply(constraints, CONSTRAINT_TYPES);
    }

    private RandomNumberGenerator createRandomNumberGenerator(long min, long max, int precision) {
        return RandomNumberGenerator.builder()
                .minInclusive(min)
                .maxExclusive(max)
                .precision(precision)
                .build();
    }
}
