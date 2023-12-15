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
            (constraints, types) -> DataHelper.streamNullableCollection(constraints).filter(constraint -> types.contains(constraint.getType())).collect(Collectors.toSet());

    @Getter(AccessLevel.PACKAGE)
    protected final ValueGenerator<T> generator;

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
        return generator.generate();
    }
}
