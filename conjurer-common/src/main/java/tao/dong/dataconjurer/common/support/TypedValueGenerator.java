package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.EntityProperty;

public interface TypedValueGenerator {

    default ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
        return switch (property.type()) {
            case TEXT-> new FormattedTextGenerator(property.getPropertyConstraints());
            case SEQUENCE -> new MutableSequenceGenerator(property.getPropertyConstraints());
            case NUMBER -> new BigDecimalGenerator(property.getPropertyConstraints());
            case DATETIME, DATE -> new DatetimeGenerator(property.getPropertyConstraints());
        };
    }
}
