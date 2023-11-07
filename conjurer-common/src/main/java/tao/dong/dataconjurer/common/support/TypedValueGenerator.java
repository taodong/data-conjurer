package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.EntityProperty;

public interface TypedValueGenerator {

    default ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
        return switch (property.type()) {
            case TEXT-> new TextGeneratorDecorator(property.getPropertyConstraints());
            case SEQUENCE -> new SequenceGeneratorDecorator(property.getPropertyConstraints());
        };
    }
}
