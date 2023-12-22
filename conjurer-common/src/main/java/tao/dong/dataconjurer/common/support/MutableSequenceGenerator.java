package tao.dong.dataconjurer.common.support;

import lombok.Getter;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.Interval;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.INTERVAL;

@Getter
public class MutableSequenceGenerator extends ValueGeneratorDecorator<Long> {

    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(INTERVAL);

    public MutableSequenceGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected ValueGenerator<Long> getDefaultGenerator() {
        return new SequenceGenerator(1L, 1L);
    }

    @Override
    protected ValueGenerator<Long> createGenerator(Set<Constraint<?>> constraints) {
        long current = 1;
        long leap = 1;
        for (var constraint : constraints) {
            if (INTERVAL == constraint.getType()) {
                var interval = (Interval)constraint;
                current = interval.getBase();
                leap = interval.getLeap();
                break;
            }
        }
        return new SequenceGenerator(current, leap);
    }

    @Override
    protected Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints) {
        return FILTER_CONSTRAINTS.apply(constraints, CONSTRAINT_TYPES);
    }

    public SequenceGenerator getSequenceGenerator() {
        return (SequenceGenerator) this.generator;
    }
}
