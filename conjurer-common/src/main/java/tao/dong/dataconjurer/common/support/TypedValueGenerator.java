package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.NumberCorrelation;

import java.util.Collection;
import java.util.Optional;

import static tao.dong.dataconjurer.common.model.ConstraintType.CORRELATION;

public interface TypedValueGenerator {

    static NumberCorrelation filterNumberCorrelation(Collection<Constraint<?>> constraints) {
        return DataHelper.streamNullableCollection(constraints).filter(c -> c.getType() == CORRELATION)
                .findFirst().map(NumberCorrelation.class::cast).orElse(null);
    }

    default ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property, V1DataProviderApi dataProviderApi) {
        var cor = filterNumberCorrelation(property.getPropertyConstraints());
        return switch (property.type()) {
            case TEXT-> new FormattedTextGenerator(property.getPropertyConstraints(), Optional.ofNullable(dataProviderApi).map(V1DataProviderApi::getCharacterGroupLookup).orElse(null));
            case SEQUENCE -> new MutableSequenceGenerator(property.getPropertyConstraints());
            case NUMBER -> cor != null ? new NumberCalculator(cor.formula(), cor.properties()) : new BigDecimalGenerator(property.getPropertyConstraints());
            case DATETIME, DATE -> cor != null ? new NumberCalculator(cor.formula(), cor.properties()) : new DatetimeGenerator(property.getPropertyConstraints());
        };
    }
}
