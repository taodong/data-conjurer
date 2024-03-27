package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.support.DataGenerateException;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DataBlueprintTest {
    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();
    private final DataProviderService dataProviderService = mock(DataProviderService.class);

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
        assertEquals("t2-alias", results.get(1).getEntityName());
        assertEquals(1, results.get(1).getPropertyTypes().size());
        assertEquals("id", results.get(1).getProperties().get(0));
        assertEquals(1, results.get(1).getValues().size());
        assertEquals("t3", results.get(2).getEntityName());
        assertEquals(2, results.get(2).getValues().size());
        assertEquals("t1", results.get(3).getEntityName());
        assertEquals(1, results.get(3).getValues().size());
    }

    private static Stream<Arguments> testVerifyCompletion() {
        return Stream.of(
                Arguments.of(Collections.emptyList()),
                Arguments.of(List.of(
                        createTestEntityDataOutput("t1", Collections.emptyList(), Collections.emptyList(), List.of(List.of(1, 2))),
                        createTestEntityDataOutput("t2", Collections.emptyList(), Collections.emptyList(), List.of(List.of(1, 2))),
                        createTestEntityDataOutput("t3", Collections.emptyList(), Collections.emptyList(), List.of(List.of(1, 2))),
                        createTestEntityDataOutput("t4", Collections.emptyList(), Collections.emptyList(), Collections.emptyList())
                ))
        );
    }

    static private EntityDataOutput createTestEntityDataOutput(String name, List<PropertyType> propertyTypes, List<String> properties, List<List<Object>> values) {
        var output = new EntityDataOutput(name, propertyTypes, properties);
        output.addValues(values);
        return output;
    }

    @ParameterizedTest
    @MethodSource
    void testVerifyCompletion(List<EntityDataOutput> results) {
        var blueprint = createTestBlueprint();
        assertThrows(DataGenerateException.class,
                () -> blueprint.verifyCompletion(results));
    }

    private DataBlueprint createTestBlueprint() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        var wrapperId = new EntityWrapperId("t3", 1);
        data.put(wrapperId, new EntityWrapper(TEST_HELPER.createEntityT3(), TEST_HELPER.createSimpleDataWithId("t3", 10L, 1), null, dataProviderService, 0));
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
