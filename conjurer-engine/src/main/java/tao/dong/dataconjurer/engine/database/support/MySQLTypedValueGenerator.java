package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;

import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

public class MySQLTypedValueGenerator implements TypedValueGenerator {

    @Override
    public ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property, V1DataProviderApi dataProviderApi) {
        if (property.type() == SEQUENCE && property.index().id() == 0) {
            return new MySQLMutableSequenceGenerator(property.getPropertyConstraints());
        } else {
            return TypedValueGenerator.super.matchDefaultGeneratorByType(property, dataProviderApi);
        }
    }
}
