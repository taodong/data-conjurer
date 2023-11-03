package tao.dong.dataconjurer.engine.database.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.EntityWrapperId;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.support.CircularDependencyChecker;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataGenerateException;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;
import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.DEPENDENCE;

class DataGenerateServiceTest {
    private final DataGenerateConfig config = mock(DataGenerateConfig.class);
    private final CircularDependencyChecker circularDependencyChecker = mock(CircularDependencyChecker.class);

    @Test
    void testGenerateData() {
        Map<EntityWrapperId, EntityWrapper> entityMap = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        DataGenerateConfig dataGenerateConfig = DataGenerateConfig.builder().handlerCount(2).build();
        CircularDependencyChecker checker = new CircularDependencyChecker();
        DataGenerateService service = new DataGenerateService(dataGenerateConfig, checker);
        createTestEntityMap(entityMap, idMap);
        Set<EntityWrapper> entities = new HashSet<>(entityMap.values());
        service.generateData(entities);
        assertEquals(10, entityMap.get(createEntityWrapperIdNoOrder("t1")).getValues().size());
        assertEquals(5, entityMap.get(createEntityWrapperIdNoOrder("t2")).getValues().size());
        assertEquals(5, entityMap.get(createEntityWrapperIdNoOrder("t3")).getValues().size());
        assertEquals(5, entityMap.get(createEntityWrapperIdNoOrder("t4")).getValues().size());
        for (var entity : entities) {
            assertEquals(2, entity.getStatus());
        }
    }

    @Test
    void testGenerateData_MultiplePlan() {
        Map<EntityWrapperId, EntityWrapper> entityMap = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        DataGenerateConfig dataGenerateConfig = DataGenerateConfig.builder().handlerCount(2).build();
        CircularDependencyChecker checker = new CircularDependencyChecker();
        DataGenerateService service = new DataGenerateService(dataGenerateConfig, checker);
        createTestEntityMapWithMultiplePlan(entityMap, idMap);
        Set<EntityWrapper> entities = new HashSet<>(entityMap.values());
        service.generateData(entities);
        assertEquals(10, entityMap.get(createEntityWrapperIdNoOrder("t1")).getValues().size());
        assertEquals(5, entityMap.get(createEntityWrapperIdNoOrder("t2")).getValues().size());
        assertEquals(5, entityMap.get(createEntityWrapperIdNoOrder("t3")).getValues().size());
        assertEquals(5, entityMap.get(createEntityWrapperIdNoOrder("t4")).getValues().size());
        assertEquals(5, entityMap.get(new EntityWrapperId("t3", 1)).getValues().size());
        for (var entity : entities) {
            assertEquals(2, entity.getStatus());
        }
    }

    @Test
    void testFailDataGeneration() {
        var wrapper1 = mockWrapperWithStatus(1);
        var wrapper2 = mockWrapperWithStatus(2);
        var wrapper3 = mockWrapperWithStatus(-1);
        var wrappers = Set.of(wrapper1, wrapper2, wrapper3);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
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
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var task = service.createDataGenerateTask(data.get(createEntityWrapperIdNoOrder("t1")), data, idMap, latch);
        assertEquals(2, task.getReferenced().size());
    }

    private void mockDataForCreateDataGenerationTaskTest(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        createTestEntityMap(data, idMap);
        var t2 = data.get(createEntityWrapperIdNoOrder("t2"));
        t2.updateStatus(1);
        t2.updateStatus(2);
        t2.createReferenced("t2p0");
        var t3 = data.get(createEntityWrapperIdNoOrder("t3"));
        t3.updateStatus(1);
        t3.updateStatus(2);
        t3.createReferenced("t3p0");
        for (var i = 0; i < 5; i++) {
            t2.getReferenced().get("t2p0").addValue((long)i);
            t3.getReferenced().get("t3p0").addValue((long)i);
        }
    }

    @Test
    void testCreateEntityMapWithReference() {
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        createTestEntityMap(data, idMap);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var processed = service.createEntityMapWithReference(new HashSet<>(data.values()), data, idMap);
        assertEquals(1, processed.get(createEntityWrapperIdNoOrder("t2")).getReferenced().size());
        assertEquals(1, processed.get(createEntityWrapperIdNoOrder("t3")).getReferenced().size());
    }

    @Test
    void testValidate() {
        when(circularDependencyChecker.hasCircular(anyMap())).thenReturn(false);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        createTestEntityMap(data, idMap);
        var raw = new HashSet<>(data.values());
        service.validate(raw);
        assertTrue(true);
    }

    @Test
    void testValidate_ThrowException() {
        when(circularDependencyChecker.hasCircular(anyMap())).thenReturn(true);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        createTestEntityMap(data, idMap);
        var raw = new HashSet<>(data.values());
        DataGenerateException exception = assertThrows(DataGenerateException.class, () -> service.validate(raw));
        assertEquals(DEPENDENCE, exception.getErrorType());
    }

    @Test
    void testFindReadyEntities() {
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
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
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        Map<EntityWrapperId, EntityWrapper> data = new HashMap<>();
        Map<String, Set<EntityWrapperId>> idMap = new HashMap<>();
        createTestDataForSelection(data, idMap);
        data.get(createEntityWrapperIdNoOrder("t4")).failProcess("negative test");
        var runners = new HashSet<EntityWrapper>();
        runners.add(data.get(createEntityWrapperIdNoOrder("t2")));
        runners.add(data.get(createEntityWrapperIdNoOrder("t3")));
        service.removeDropouts(runners, data, idMap);
        assertEquals(1, runners.size());
        assertEquals(-1, data.get(createEntityWrapperIdNoOrder("t3")).getStatus());
    }

    private void createTestDataForSelection(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        createTestEntityMap(data, idMap);
        var t2 = data.get(createEntityWrapperIdNoOrder("t2"));
        t2.createReferenced("t2p0");
        var t3 = data.get(createEntityWrapperIdNoOrder("t3"));
        t3.createReferenced("t3p0");
        var t4 = data.get(createEntityWrapperIdNoOrder("t4"));
        t4.createReferenced("t4p0");
    }

    private void createTestEntityMapWithMultiplePlan(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        createTestEntityMap(data, idMap);
        var entity5 = new DataEntity("t3",
                Set.of(
                        new EntityProperty("t3p0", SEQUENCE, true, 1, List.of(new Interval(1L, 6L)), null),
                        new EntityProperty("t3p1", SEQUENCE, true, -1, null, new Reference("t4", "t4p0"))
                )
        );
        var wrapper5 = new EntityWrapper(entity5, new EntityData("t3", 1, 5L));
        data.put(wrapper5.getId(), wrapper5);
        DataHelper.appendToSetValueInMap(idMap, wrapper5.getEntityName(), wrapper5.getId());
    }

    private void createTestEntityMap(Map<EntityWrapperId, EntityWrapper> data, Map<String, Set<EntityWrapperId>> idMap) {
        var wrapper1 = getEntityWrapper1();
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

    private static EntityWrapper getEntityWrapper1() {
        var entity1 = new DataEntity("t1",
                Set.of(
                        new EntityProperty("t1p1", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t1p2", SEQUENCE, true, -1, null, new Reference("t2", "t2p0")),
                        new EntityProperty("t1p3", TEXT, true, -1, List.of(new Length(10L)), null),
                        new EntityProperty("t1p4", SEQUENCE, true, -1, null, new Reference("t3", "t3p0"))
                )
        );
        return new EntityWrapper(entity1, new EntityData("t1", 10L));
    }

    private static EntityWrapperId createEntityWrapperIdNoOrder(String entityName) {
        return new EntityWrapperId(entityName, 0);
    }

}
