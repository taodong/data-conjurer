package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.NumberCorrelation;

import java.util.Optional;

import static tao.dong.dataconjurer.common.model.ConstraintType.CORRELATION;

public interface TypedValueGenerator {

    default ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property, V1DataProviderApi dataProviderApi) {
        return switch (property.type()) {
            case TEXT-> new FormattedTextGenerator(property.getPropertyConstraints(), Optional.ofNullable(dataProviderApi).map(V1DataProviderApi::getCharacterGroupLookup).orElse(null));
            case SEQUENCE -> new MutableSequenceGenerator(property.getPropertyConstraints());
            case NUMBER -> {
                var cor = property.getPropertyConstraints().stream().filter(c -> c.getType() == CORRELATION).findFirst();
                if (cor.isPresent()) {
                    var constraint = (NumberCorrelation)cor.get();
                    yield new NumberCalculator(constraint.formula(), constraint.properties());
                } else {
                    yield new BigDecimalGenerator(property.getPropertyConstraints());
                }
            }
            case DATETIME, DATE -> new DatetimeGenerator(property.getPropertyConstraints());
        };
    }
}
