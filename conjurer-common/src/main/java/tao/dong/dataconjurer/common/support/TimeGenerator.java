package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.NumberRange;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.NUMBER_RANGE;

public class TimeGenerator extends ValueGeneratorDecorator<Long> {
    public static final Long MINUS_839_HOURS = -(838L * 60 * 60 + 59 * 60 + 59);
    public static final Long PLUS_839_HOURS = 839L * 60 * 60;

    private static final Set<ConstraintType> CONSTRAINT_TYPES = Set.of(NUMBER_RANGE);
    public TimeGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected ValueGenerator<Long> getDefaultGenerator() {
        qualifiedConstraints.add(new NumberRange(MINUS_839_HOURS, PLUS_839_HOURS));
        return createRandomLongGenerator(MINUS_839_HOURS, PLUS_839_HOURS);
    }

    @Override
    protected ValueGenerator<Long> createGenerator(Set<Constraint<?>> constraints) {
        var min = MINUS_839_HOURS;
        var max = PLUS_839_HOURS;
        for (var constraint : constraints) {
            if (constraint.getType() == NUMBER_RANGE) {
                var range = (NumberRange) constraint;
                min = range.getMin();
                max = range.getMax();
            }
        }
        return createRandomLongGenerator(min, max);
    }

    private RandomLongGenerator createRandomLongGenerator(long min, long max) {
        return RandomLongGenerator
                .builder()
                .minInclusive(min)
                .maxExclusive(max)
                .build();
    }

    @Override
    protected Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints) {
        return FILTER_CONSTRAINTS.apply(constraints, CONSTRAINT_TYPES);
    }

    @Override
    protected void testConstraints(Long val) {
        testConstraints(val, constraint -> constraint.getType() == NUMBER_RANGE && !((NumberRange) constraint).isMet(val));
    }
}
