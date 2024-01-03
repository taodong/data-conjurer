package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ValueGeneratorDecorator<T> implements ValueGenerator<T> {
    static final BiFunction<Set<Constraint<?>>, Set<ConstraintType>, Set<Constraint<?>>> FILTER_CONSTRAINTS =
            (constraints, types) -> DataHelper.streamNullableCollection(constraints).filter(constraint -> types.contains(constraint.getType())).collect(Collectors.toSet());

    @Getter(AccessLevel.PACKAGE)
    protected final ValueGenerator<T> generator;
    protected final List<Constraint<?>> qualifiedConstraints = new ArrayList<>();

    /**
     * Short circuit for another abstract child only
     */
    protected ValueGeneratorDecorator(ValueGenerator<T> generator) {
        this.generator = generator;
    }

    protected ValueGeneratorDecorator(Set<Constraint<?>> constraints) {
        this.generator = createGeneratorByConstraints(constraints);
    }

    protected ValueGenerator<T> createGeneratorByConstraints(Set<Constraint<?>> constraints) {
        var qualified = filterConstraints(constraints);
        if (!qualified.isEmpty()) {
            qualifiedConstraints.addAll(qualified);
            return createGenerator(qualified);
        } else {
            return getDefaultGenerator();
        }
    }

    protected abstract ValueGenerator<T> getDefaultGenerator();
    protected abstract ValueGenerator<T> createGenerator(Set<Constraint<?>> constraints);
    protected abstract Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints);

    @Override
    public T generate() {
        var val = generator.generate();
        testConstraints(val);
        return val;
    }

    protected abstract void testConstraints(T val);

    protected void testConstraints(T val, Predicate<Constraint<?>> constraintFailure) {
        var violation = qualifiedConstraints.stream().filter(constraintFailure).findFirst();

        if (violation.isPresent()) {
            throw new ConstraintViolationException(
                    "Generated value %s violated constraint %s".formatted(val.toString(), violation.get().getType().name()));
        }
    }
}
