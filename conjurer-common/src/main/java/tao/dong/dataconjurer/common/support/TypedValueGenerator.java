package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.EntityProperty;

import java.util.Optional;

public interface TypedValueGenerator {

    default ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property, V1DataProviderApi dataProviderApi) {
        return switch (property.type()) {
            case TEXT-> new FormattedTextGenerator(property.getPropertyConstraints(), Optional.ofNullable(dataProviderApi).map(V1DataProviderApi::getCharacterGroupLookup).orElse(null));
            case SEQUENCE -> new MutableSequenceGenerator(property.getPropertyConstraints());
            case NUMBER -> new BigDecimalGenerator(property.getPropertyConstraints());
            case DATETIME, DATE -> new DatetimeGenerator(property.getPropertyConstraints());
        };
    }
}
