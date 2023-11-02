package tao.dong.dataconjurer.engine.database.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.*;
import tao.dong.dataconjurer.common.support.CircularDependencyChecker;
import tao.dong.dataconjurer.common.support.DataGenerateConfig;
import tao.dong.dataconjurer.common.support.DataGenerateException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;
import static tao.dong.dataconjurer.common.support.DataGenerationErrorType.DEPENDENCE;

class DataGenerateServiceTest {
    private final DataGenerateConfig config = mock(DataGenerateConfig.class);
    private final CircularDependencyChecker circularDependencyChecker = mock(CircularDependencyChecker.class);

    @Test
    void testGenerateData() {
        DataGenerateConfig dataGenerateConfig = DataGenerateConfig.builder().handlerCount(2).build();
        CircularDependencyChecker checker = new CircularDependencyChecker();
        DataGenerateService service = new DataGenerateService(dataGenerateConfig, checker);
        var entityMap = createTestEntityMap();
        Set<EntityWrapper> entities = new HashSet<>(entityMap.values());
        service.generateData(entities);
        assertEquals(10, entityMap.get("t1").getValues().size());
        assertEquals(5, entityMap.get("t2").getValues().size());
        assertEquals(5, entityMap.get("t3").getValues().size());
        assertEquals(5, entityMap.get("t4").getValues().size());
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
        var data = mockDataForCreateDataGenerationTaskTest();
        CountDownLatch latch = new CountDownLatch(1);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var task = service.createDataGenerateTask(data.get("t1"), data, latch);
        assertEquals(2, task.getReferenced().size());
    }

    private Map<String, EntityWrapper> mockDataForCreateDataGenerationTaskTest() {
        var data = createTestEntityMap();
        var t2 = data.get("t2");
        t2.updateStatus(1);
        t2.updateStatus(2);
        t2.createReferenced("t2p0");
        var t3 = data.get("t3");
        t3.updateStatus(1);
        t3.updateStatus(2);
        t3.createReferenced("t3p0");
        for (var i = 0; i < 5; i++) {
            t2.getReferenced().get("t2p0").addValue((long)i);
            t3.getReferenced().get("t3p0").addValue((long)i);
        }
        return data;
    }

    @Test
    void testCreateEntityMapWithReference() {
        var raw = createTestEntityMap();
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var processed = service.createEntityMapWithReference(new HashSet<>(raw.values()));
        assertEquals(1, processed.get("t2").getReferenced().size());
        assertEquals(1, processed.get("t3").getReferenced().size());
    }

    @Test
    void testValidate() {
        when(circularDependencyChecker.hasCircular(anyMap())).thenReturn(false);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var raw = new HashSet<>(createTestEntityMap().values());
        service.validate(raw);
        assertTrue(true);
    }

    @Test
    void testValidate_ThrowException() {
        when(circularDependencyChecker.hasCircular(anyMap())).thenReturn(true);
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var raw = new HashSet<>(createTestEntityMap().values());
        DataGenerateException exception = assertThrows(DataGenerateException.class, () -> service.validate(raw));
        assertEquals(DEPENDENCE, exception.getErrorType());
    }

    @Test
    void testFindReadyEntities() {
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var data = createTestDataForSelection();
        var runners = service.findReadyEntities(data);
        assertEquals(2, runners.size());
        for (var en : runners) {
            assertTrue(StringUtils.endsWithAny(en.getEntityName(), "t2", "t4"));
        }
    }

    @Test
    void testRemoveDropouts() {
        DataGenerateService service = new DataGenerateService(config, circularDependencyChecker);
        var data = createTestDataForSelection();
        data.get("t4").failProcess("negative test");
        var runners = new HashSet<EntityWrapper>();
        runners.add(data.get("t2"));
        runners.add(data.get("t3"));
        service.removeDropouts(runners, data);
        assertEquals(1, runners.size());
        assertEquals(-1, data.get("t3").getStatus());
    }

    private Map<String, EntityWrapper> createTestDataForSelection() {
        var data = createTestEntityMap();
        var t2 = data.get("t2");
        t2.createReferenced("t2p0");
        var t3 = data.get("t3");
        t3.createReferenced("t3p0");
        var t4 = data.get("t4");
        t4.createReferenced("t4p0");
        return data;
    }

    Map<String, EntityWrapper> createTestEntityMap() {
        Map<String, EntityWrapper> data = new HashMap<>();
        var wrapper1 = getEntityWrapper1();
        data.put("t1", wrapper1);

        var entity2 = new DataEntity("t2",
                Set.of(
                        new EntityProperty("t2p0", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t2p1", TEXT, true, -1, List.of(new Length(15L)), null)
                )
        );
        var wrapper2 = new EntityWrapper(entity2, new EntityData("t2", 5L));
        data.put("t2", wrapper2);

        var entity3 = new DataEntity("t3",
                Set.of(
                        new EntityProperty("t3p0", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                        new EntityProperty("t3p1", SEQUENCE, true, -1, null, new Reference("t4", "t4p0"))
                )
        );
        var wrapper3 = new EntityWrapper(entity3, new EntityData("t3", 5L));
        data.put("t3", wrapper3);

        var entity4 = new DataEntity("t4",
                Set.of(
                        new EntityProperty("t4p0", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null)
                )
        );
        var wrapper4 = new EntityWrapper(entity4, new EntityData("t4", 5L));
        data.put("t4", wrapper4);
        return data;
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

}
