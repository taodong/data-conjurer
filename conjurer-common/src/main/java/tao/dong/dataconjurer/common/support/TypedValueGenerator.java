package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.EntityProperty;

import java.util.Set;
import java.util.function.Function;

public interface TypedValueGenerator {

    static ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
        return switch (property.type()) {
            case TEXT-> new TextGeneratorDecorator(property.getPropertyConstraints());
            case SEQUENCE -> new SequenceGeneratorDecorator(property.getPropertyConstraints());
        };
    }
}
