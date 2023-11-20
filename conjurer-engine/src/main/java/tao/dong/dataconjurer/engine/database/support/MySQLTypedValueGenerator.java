package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;

import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

public class MySQLTypedValueGenerator implements TypedValueGenerator {

    @Override
    public ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
        if (property.type() == SEQUENCE && property.idIndex() == 1) {
            return new MySQLMutableSequenceGenerator(property.getPropertyConstraints());
        } else {
            return TypedValueGenerator.super.matchDefaultGeneratorByType(property);
        }
    }
}
