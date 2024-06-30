package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.StringAlternation;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.NumberCorrelation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CORRELATION;

public interface TypedValueGenerator {

    static <T extends Constraint<?>> T filterConstraintByType(Collection<T> constraints, ConstraintType type) {
        return DataHelper.streamNullableCollection(constraints).filter(c -> c.getType() == type).findFirst().orElse(null);
    }

    default ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property, DataProviderService dataProviderService) {
        var cor = (NumberCorrelation)filterConstraintByType(property.getPropertyConstraints(), CORRELATION);
        var alt = (StringAlternation)filterConstraintByType(property.getPropertyConstraints(), ConstraintType.ALTERNATION);
        return switch (property.type()) {
            case TEXT-> alt != null ? new StringTransformer(alt.formula(), alt.properties()) : new FormattedTextGenerator(property.getPropertyConstraints(), Optional.ofNullable(dataProviderService).map(DataProviderService::getCharacterGroupLookup).orElse(null));
            case SEQUENCE -> new MutableSequenceGenerator(property.getPropertyConstraints());
            case NUMBER -> cor != null ? new NumberCalculator(cor.formula(), cor.properties()) : new BigDecimalGenerator(property.getPropertyConstraints());
            case DATETIME -> cor != null ? new NumberCalculator(cor.formula(), cor.properties()) : new DatetimeGenerator(property.getPropertyConstraints());
            case DATE -> cor != null ? new NumberCalculator(cor.formula(), cor.properties()) : new DateGenerator(property.getPropertyConstraints());
            case TIME -> cor != null ? new NumberCalculator(cor.formula(), cor.properties()) : new TimeGenerator(property.getPropertyConstraints());
            case BOOLEAN -> new ElectedValueSelector(Set.of(true, false));
        };
    }
}
