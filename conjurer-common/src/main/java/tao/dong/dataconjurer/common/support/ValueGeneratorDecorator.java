package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class ValueGeneratorDecorator<T> implements ValueGenerator<T> {
    static final BiFunction<Set<Constraint<?>>, Set<ConstraintType>, Set<Constraint<?>>> FILTER_CONSTRAINTS =
            (constraints, types) -> constraints.stream().filter(constraint -> types.contains(constraint.getType())).collect(Collectors.toSet());

    @Getter(AccessLevel.PACKAGE)
    protected final ValueGenerator<T> generator;

    protected ValueGeneratorDecorator(Set<Constraint<?>> constraints) {
        var qualified = filterConstraints(constraints);
        if (!qualified.isEmpty()) {
            generator = createGenerator(qualified);
        } else {
            generator = getDefaultGenerator();
        }
    }

    protected abstract ValueGenerator<T> getDefaultGenerator();
    protected abstract ValueGenerator<T> createGenerator(Set<Constraint<?>> constraints);
    protected abstract Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints);

    @Override
    public T generate() {
        return generator.generate();
    }
}
