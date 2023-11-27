package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityOutputControl;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.support.TypedValueGenerator;
import tao.dong.dataconjurer.common.support.ValueGenerator;
import tao.dong.dataconjurer.engine.database.support.MySQLTypedValueGenerator;

public class MySQLEntityWrapper extends EntityWrapper {

    public MySQLEntityWrapper(DataEntity entity, EntityData data, EntityOutputControl outputControl, V1DataProviderApi dataProviderApi) {
        super(entity, data, outputControl, dataProviderApi);
    }

    @Override
    protected ValueGenerator<?> matchFallbackValueGenerator(EntityProperty property) {
        TypedValueGenerator typedValueGenerator = new MySQLTypedValueGenerator();
        return typedValueGenerator.matchDefaultGeneratorByType(property, dataProviderApi);
    }
}
