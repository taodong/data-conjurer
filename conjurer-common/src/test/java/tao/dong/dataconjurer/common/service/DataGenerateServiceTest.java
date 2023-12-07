package tao.dong.dataconjurer.common.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;
import tao.dong.dataconjurer.common.model.DataBlueprint;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.PropertyLink;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.SimpleTypedValue;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataHelper;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataGenerateServiceTest {
    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();
    private final V1DataProviderApi dataProviderApi = mock(V1DataProviderApi.class);

    @Test
    void testGenerateData() {
        Map<EntityWrapperId, EntityWrapper> entityMap = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        DataGenerateConfig dataGenerateConfig = DataGenerateConfig.builder().build();
        DataGenerateService service = new DataGenerateService();
        TEST_HELPER.createSimpleBlueprintDataWithReference(entityMap, idMap);
        var blueprint = new DataBlueprint();
        blueprint.init(entityMap, idMap);
        service.generateData(blueprint, dataGenerateConfig);
        assertEquals(10, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t1")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t4")).getValues().size());
        for (var entity : blueprint.getEntities().values()) {
            assertEquals(2, entity.getStatus());
        }
    }

    @Test
    void testGenerateData_MultiplePlan() {
        Map<EntityWrapperId, EntityWrapper> entityMap = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        DataGenerateConfig dataGenerateConfig = DataGenerateConfig.builder().build();
        DataGenerateService service = new DataGenerateService();
        createTestEntityMapWithMultiplePlan(entityMap, idMap);
        var blueprint = new DataBlueprint();
        blueprint.init(entityMap, idMap);
        service.generateData(blueprint, dataGenerateConfig);
        assertEquals(10, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t1")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(TEST_HELPER.createEntityWrapperIdNoOrder("t4")).getValues().size());
        assertEquals(5, blueprint.getEntities().get(new EntityWrapperId("t3", 1)).getValues().size());
        for (var entity : blueprint.getEntities().values()) {
            assertEquals(2, entity.getStatus());
        }
    }

    @Test
    void testFailDataGeneration() {
        var wrapper1 = mockWrapperWithStatus(1);
        var wrapper2 = mockWrapperWithStatus(2);
        var wrapper3 = mockWrapperWithStatus(-1);
        var wrappers = Set.of(wrapper1, wrapper2, wrapper3);
        DataGenerateService service = new DataGenerateService();
        service.failDataGeneration(wrappers, "test error");
        verify(wrapper1, times(1)).failProcess("test error");
        verify(wrapper2, never()).failProcess(anyString());
        verify(wrapper3, never()).failProcess(anyString());
    }

    private EntityWrapper mockWrapperWithStatus(int status) {
        EntityWrapper wrapper = mock(EntityWrapper.class);
        when(wrapper.getStatus()).thenReturn(status);
        doNothing().when(wrapper).failProcess(anyString());
        return wrapper;
    }

    @Test
    void testCreateDataGenerateTask() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        mockDataForCreateDataGenerationTaskTest(data, idMap);
        CountDownLatch latch = new CountDownLatch(1);
        DataGenerateService service = new DataGenerateService();
        var config = DataGenerateConfig.builder().build();
        var task = service.createDataGenerateTask(data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t1")), data, idMap, latch, config);
        assertEquals(2, task.getReferenced().size());
    }

    private void mockDataForCreateDataGenerationTaskTest(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        TEST_HELPER.createSimpleBlueprintData(data, idMap);
        var t2 = data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t2"));
        t2.updateStatus(1);
        t2.updateStatus(2);
        t2.createReferenced(new PropertyLink("t2p0", null));
        var t3 = data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t3"));
        t3.updateStatus(1);
        t3.updateStatus(2);
        t3.createReferenced(new PropertyLink("t3p0", null));
        for (var i = 0; i < 5; i++) {
            ((SimpleTypedValue)t2.getReferenced().get("t2p0")).addValue((long)i);
            ((SimpleTypedValue)t3.getReferenced().get("t3p0")).addValue((long)i);
        }
    }

    @Test
    void testFindReadyEntities() {
        DataGenerateService service = new DataGenerateService();
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        createTestDataForSelection(data, idMap);
        var runners = service.findReadyEntities(data, idMap);
        assertEquals(2, runners.size());
        for (var en : runners) {
            assertTrue(StringUtils.endsWithAny(en.getEntityName(), "t2", "t4"));
        }
    }

    @Test
    void testRemoveDropouts() {
        DataGenerateService service = new DataGenerateService();
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        createTestDataForSelection(data, idMap);
        data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t4")).failProcess("negative test");
        var runners = new HashSet<EntityWrapper>();
        runners.add(data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t2")));
        runners.add(data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")));
        service.removeDropouts(runners, data, idMap);
        assertEquals(1, runners.size());
        assertEquals(-1, data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t3")).getStatus());
    }

    private void createTestDataForSelection(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        TEST_HELPER.createSimpleBlueprintData(data, idMap);
        var t2 = data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t2"));
        t2.createReferenced(new PropertyLink("t2p0", null));
        var t3 = data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t3"));
        t3.createReferenced(new PropertyLink("t3p0", null));
        var t4 = data.get(TEST_HELPER.createEntityWrapperIdNoOrder("t4"));
        t4.createReferenced(new PropertyLink("t4p0",null));
    }

    private void createTestEntityMapWithMultiplePlan(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        TEST_HELPER.createSimpleBlueprintDataWithReference(data, idMap);
        var entity5 = new DataEntity("t3",
                Set.of(
                        EntityTestHelper.entityPropertyBuilder().name("t3p0").index(EntityTestHelper.entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 6L))).build(),
                        EntityTestHelper.entityPropertyBuilder().name("t3p1").reference(new Reference("t4", "t4p0", null)).build()
                )
        );
        var wrapper5 = new EntityWrapper(entity5, new EntityData("t3", 1, 5L, null, null), null, dataProviderApi);
        data.put(wrapper5.getId(), wrapper5);
        DataHelper.appendToSetValueInMap(idMap, wrapper5.getEntityName(), wrapper5.getId());
    }
}
