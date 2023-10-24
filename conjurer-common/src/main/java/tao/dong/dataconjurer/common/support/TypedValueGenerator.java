package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.EntityProperty;

import java.util.Set;

public interface TypedValueGenerator {

    static ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
        return switch (property.type()) {
            case TEXT-> new TextGeneratorDecorator(Set.copyOf(property.constraints()));
            case SEQUENCE -> new SequenceGeneratorDecorator(Set.copyOf(property.constraints()));
        };
    }
}
