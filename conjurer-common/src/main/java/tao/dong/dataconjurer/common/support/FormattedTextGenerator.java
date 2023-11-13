package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.UnfixedSize;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.LENGTH;
import static tao.dong.dataconjurer.common.model.ConstraintType.SIZE;

public class FormattedTextGenerator extends ValueGeneratorDecorator<String> {
    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(LENGTH, SIZE);

    public FormattedTextGenerator(Set<Constraint<?>> constraints) {
        super(constraints);
    }

    @Override
    protected ValueGenerator<String> getDefaultGenerator() {
        return new RangeLengthStringGenerator(1, 100);
    }

    @Override
    protected ValueGenerator<String> createGenerator(Set<Constraint<?>> constraints) {
        for (var constraint : constraints) {
            if (LENGTH ==constraint.getType()) {
                var length = Math.toIntExact(((Length)constraint).getMax());
                return new FixLengthStringGenerator(length);
            } else if (SIZE == constraint.getType()) {
                var min = Math.toIntExact(((UnfixedSize)constraint).getMin());
                var max = Math.toIntExact(((UnfixedSize)constraint).getMax());
                return new RangeLengthStringGenerator(min, max);
            }
        }

        return getDefaultGenerator();
    }

    @Override
    protected Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints) {
        return FILTER_CONSTRAINTS.apply(constraints, CONSTRAINT_TYPES);
    }
}
