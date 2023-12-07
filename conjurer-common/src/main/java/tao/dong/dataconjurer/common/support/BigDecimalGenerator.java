package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.NumberRange;
import tao.dong.dataconjurer.common.model.Precision;

import java.math.BigDecimal;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CORRELATION;
import static tao.dong.dataconjurer.common.model.ConstraintType.NUMBER_RANGE;
import static tao.dong.dataconjurer.common.model.ConstraintType.PRECISION;


public class BigDecimalGenerator extends ValueGeneratorDecorator<BigDecimal>{
    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(NUMBER_RANGE, PRECISION, CORRELATION);

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
        for (var constraint : constraints) {
            if (NUMBER_RANGE == constraint.getType()) {
                var range = (NumberRange) constraint;
                max = range.getMax();
                min = range.getMin();
            } else if (PRECISION == constraint.getType()) {
                precision = ((Precision)constraint).getMax();
            }
        }
        return createRandomNumberGenerator(min, max, precision);
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
