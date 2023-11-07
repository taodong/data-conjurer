package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataBlueprintTest {
    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();

    @Test
    void testSortEntityByDependencies() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        TEST_HELPER.createSimpleBlueprintDataWithReference(data, idMap);
        var blueprint = new DataBlueprint();
        blueprint.init(data, idMap);
        for (var entity : blueprint.getEntities().values()) {
            entity.updateStatus(1);
            entity.updateStatus(2);
        }
        var sorted = blueprint.sortEntityByDependencies();
        assertEquals(4, sorted.size());
        assertEquals("t4", sorted.get(0).getEntityName());
        assertEquals("t2", sorted.get(1).getEntityName());
        assertEquals("t3", sorted.get(2).getEntityName());
        assertEquals("t1", sorted.get(3).getEntityName());
    }

    @Test
    void testSortEntityByDependencies_MultiplePlans() {
        var blueprint = createTestBlueprint();
        var sorted = blueprint.sortEntityByDependencies();
        assertEquals(5, sorted.size());
        assertEquals("t4", sorted.get(0).getEntityName());
        assertEquals("t2", sorted.get(1).getEntityName());
        assertEquals("t3", sorted.get(2).getEntityName());
        assertEquals("t3", sorted.get(3).getEntityName());
        assertEquals("t1", sorted.get(4).getEntityName());
    }

    @Test
    void testOutputGeneratedData() {
        var blueprint = createTestBlueprint();
        var results = blueprint.outputGeneratedData();
        assertEquals(4, results.size());
        assertEquals("t4", results.get(0).getEntityName());
        assertEquals(1, results.get(0).getValues().size());
        assertEquals("t2", results.get(1).getEntityName());
        assertEquals(1, results.get(1).getValues().size());
        assertEquals("t3", results.get(2).getEntityName());
        assertEquals(2, results.get(2).getValues().size());
        assertEquals("t1", results.get(3).getEntityName());
        assertEquals(1, results.get(3).getValues().size());
    }

    private DataBlueprint createTestBlueprint() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        var wrapperId = new EntityWrapperId("t3", 1);
        data.put(wrapperId, new EntityWrapper(TEST_HELPER.createEntityT3(), TEST_HELPER.createSimpleDataWithId("t3", 10L, 1)));
        DataHelper.appendToSetValueInMap(idMap, "t3", wrapperId);
        TEST_HELPER.createSimpleBlueprintDataWithReference(data, idMap);
        var blueprint = new DataBlueprint();
        blueprint.init(data, idMap);
        for (var entity : blueprint.getEntities().values()) {
            entity.updateStatus(1);
            entity.updateStatus(2);
        }
        blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t1")).getValues().add(List.of(0, 0, "abc", 0));
        blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")).getValues().add(List.of(0, "efg"));
        blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")).getValues().add(List.of(0, 0));
        blueprint.getEntities().get(wrapperId).getValues().add(List.of(1, 0));
        blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t4")).getValues().add(List.of(0));
        return blueprint;
    }

}
