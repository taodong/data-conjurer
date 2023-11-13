package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.Duration;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.DURATION;

public class DatetimeGenerator extends ValueGeneratorDecorator<Long>{
    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(DURATION);

    public DatetimeGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected ValueGenerator<Long> getDefaultGenerator() {
        return createRandomLongGenerator(Duration.OLDEST_TIME, System.currentTimeMillis());
    }

    @Override
    protected ValueGenerator<Long> createGenerator(Set<Constraint<?>> constraints) {
        var min = Duration.OLDEST_TIME;
        var max = System.currentTimeMillis();
        for (var constraint : constraints) {
            if (constraint.getType() == DURATION) {
                var duration = (Duration) constraint;
                min = duration.getMin();
                max = duration.getMax();
                break;
            }
        }
        return createRandomLongGenerator(min, max);
    }

    @Override
    protected Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints) {
        return FILTER_CONSTRAINTS.apply(constraints, CONSTRAINT_TYPES);
    }

    private RandomLongGenerator createRandomLongGenerator(long min, long max) {
        return RandomLongGenerator
                .builder()
                .minInclusive(min)
                .maxExclusive(max)
                .build();
    }

}
