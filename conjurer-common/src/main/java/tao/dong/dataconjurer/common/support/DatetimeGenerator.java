package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.ChainedValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.Duration;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CHAIN;
import static tao.dong.dataconjurer.common.model.ConstraintType.DURATION;

public class DatetimeGenerator extends ValueGeneratorDecorator<Long>{
    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(DURATION, CHAIN);

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
        ChainedValue chainedValue = null;
        for (var constraint : constraints) {
            if (constraint.getType() == DURATION) {
                var duration = (Duration) constraint;
                min = Math.max(min, duration.getMin());
                max = Math.min(max, duration.getMax());
            } else if (constraint.getType() == CHAIN) {
                chainedValue = (ChainedValue)  constraint;
            }
        }

        if (chainedValue != null) {
            return switch (Integer.compare(0, chainedValue.getDirection())) {
                case 1 -> new ChainedLongGenerator(1, chainedValue.getSeed(), chainedValue.getStyle(), createRandomLongGenerator(min, min + (long) (chainedValue.getSeed() * 2)).generate());
                case -1 -> new ChainedLongGenerator(-1, chainedValue.getSeed(), chainedValue.getStyle(), createRandomLongGenerator(max - (long) (2 * chainedValue.getSeed()), max).generate());
                default -> new ChainedLongGenerator(0, chainedValue.getSeed(), chainedValue.getStyle(), createRandomLongGenerator(min, max).generate());
            };
        } else {
            return createRandomLongGenerator(min, max);
        }
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
