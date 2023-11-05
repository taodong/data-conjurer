package tao.dong.dataconjurer.common.support;

import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.UnfixedSize;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

public class EntityTestHelper {

    public EntityWrapperId createEntityWrapperIdNoOrder(String entityName) {
        return new EntityWrapperId(entityName, 0);
    }

    public void createSimpleBlueprintDataWithReference(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        createSimpleBlueprintData(data, idMap);
        data.get(createEntityWrapperIdNoOrder("t2")).createReferenced("t2p0");
        data.get(createEntityWrapperIdNoOrder("t3")).createReferenced("t3p0");
        data.get(createEntityWrapperIdNoOrder("t4")).createReferenced("t4p0");
    }


    public void createSimpleBlueprintData(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        var wrapper1 = getSimpleEntityWrapper();
        data.put(wrapper1.getId(), wrapper1);
        DataHelper.appendToSetValueInMap(idMap, wrapper1.getEntityName(), wrapper1.getId());

        var entity2 = new DataEntity("t2",
                Set.of(
                        new EntityProperty("t2p0", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t2p1", TEXT, true, -1, List.of(new Length(15L)), null)
                )
        );
        var wrapper2 = new EntityWrapper(entity2, new EntityData("t2", 5L));
        data.put(wrapper2.getId(), wrapper2);
        DataHelper.appendToSetValueInMap(idMap, wrapper2.getEntityName(), wrapper2.getId());

        var entity3 = new DataEntity("t3",
                Set.of(
                        new EntityProperty("t3p0", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t3p1", SEQUENCE, true, -1, null, new Reference("t4", "t4p0"))
                )
        );
        var wrapper3 = new EntityWrapper(entity3, new EntityData("t3", 5L));
        data.put(wrapper3.getId(), wrapper3);
        DataHelper.appendToSetValueInMap(idMap, wrapper3.getEntityName(), wrapper3.getId());

        var entity4 = new DataEntity("t4",
                Set.of(
                        new EntityProperty("t4p0", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null)
                )
        );
        var wrapper4 = new EntityWrapper(entity4, new EntityData("t4", 5L));
        data.put(wrapper4.getId(), wrapper4);
        DataHelper.appendToSetValueInMap(idMap, wrapper4.getEntityName(), wrapper4.getId());
    }

    private EntityWrapper getSimpleEntityWrapper() {
        return new EntityWrapper(createSimpleEntity(), createSimpleData(null, null));
    }

    public DataEntity createSimpleEntity() {
        return new DataEntity("t1",
                Set.of(
                        new EntityProperty("t1p1", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t1p2", SEQUENCE, true, -1, null, new Reference("t2", "t2p0")),
                        new EntityProperty("t1p3", TEXT, false, -1, List.of(new UnfixedSize(15L)), null),
                        new EntityProperty("t1p4", SEQUENCE, true, -1, null, new Reference("t3", "t3p0"))
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
