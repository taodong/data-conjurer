package tao.dong.dataconjurer.engine.database.model;

import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityOutputControl;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.support.ValueGenerator;
import tao.dong.dataconjurer.engine.database.support.MySQLMutableSequenceGenerator;

import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

public class MySQLEntityWrapper extends EntityWrapper {
    public MySQLEntityWrapper(DataEntity entity, EntityData data) {
        this(entity, data, null);
    }

    public MySQLEntityWrapper(DataEntity entity, EntityData data, EntityOutputControl outputControl) {
        super(entity, data, outputControl);
    }

    @Override
    protected ValueGenerator<?> matchValueGenerator(EntityProperty property) {
        if (property.type() == SEQUENCE && property.idIndex() == 1) {
            return new MySQLMutableSequenceGenerator(property.getPropertyConstraints());
        } else {
            return super.matchValueGenerator(property);
        }
    }
}
