package tao.dong.dataconjurer.engine.database.support;

import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;

public class MySQLTypedValueGenerator implements TypedValueGenerator {
    protected TypedValueGenerator typedValueGenerator = new MySQLTypedValueGenerator();

    @Override
    public ValueGenerator<?> matchDefaultGeneratorByType(EntityProperty property) {
        return TypedValueGenerator.super.matchDefaultGeneratorByType(property);
    }
}
