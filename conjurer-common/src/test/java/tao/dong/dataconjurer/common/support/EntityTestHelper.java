package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.UnfixedSize;

import java.util.List;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

public class EntityTestHelper {
    public DataEntity createSimpleEntity() {
        return new DataEntity("t1",
                Set.of(
                        new EntityProperty("t1p1", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t1p2", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t1p3", TEXT, false, -1, List.of(new UnfixedSize(15L)), null)
                )
        );
    }

    public EntityData createSimpleData(String entityName, Long count) {
        return new EntityData(entityName == null ? "t1" : entityName,
                count == null ? 10L : count);
    }

    public EntityData createSimpleDataWithId(String entityName, Long count, int id) {
        return new EntityData(entityName == null ? "t1" : entityName, id, count == null ? 10L : count);
    }
}
