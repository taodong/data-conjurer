package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.LinkedTypedValue;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.PropertyLink;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.SimpleTypedValue;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;
import static tao.dong.dataconjurer.common.support.EntityTestHelper.entityIndexBuilder;
import static tao.dong.dataconjurer.common.support.EntityTestHelper.entityPropertyBuilder;

class DataGenerateTaskTest {
    private static final EntityTestHelper TEST_HELPER = new EntityTestHelper();

    private final DataProviderService dataProviderService = mock(DataProviderService.class);

    @Test
    void testCall_GenerateData() {
        var countDownLatch = new CountDownLatch(1);
        var config = DataGenerateConfig.builder().build();
        var data = new EntityData("t1", 10L, null, null);
        var wrapper = createTestEntityWrapper(data);
        SimpleTypedValue ref1 = new SimpleTypedValue(SEQUENCE);
        for (long l = 1L; l < 6L; l++) {
            ref1.addValue(l);
        }
        Map<Reference, TypedValue> referenced = Map.of(
                new Reference("t2", "p0", null), ref1
        );
        var task = DataGenerateTask.builder()
                .countDownLatch(countDownLatch)
                .entityWrapper(wrapper)
                .config(config)
                .referenced(referenced)
                .build();
        var result = task.call();
        assertEquals(2, result.status());
        assertEquals(10, wrapper.getValues().size());
        assertFalse(((SimpleTypedValue)wrapper.getReferenced().get("p1")).getValues().isEmpty());
    }

    private EntityWrapper createTestEntityWrapper(EntityData data) {
        var entity = new DataEntity("t1",
           Set.of(
                   EntityTestHelper.entityPropertyBuilder().name("p1").index(EntityTestHelper.entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                   EntityTestHelper.entityPropertyBuilder().name("p2").reference(new Reference("t2", "p0", null)).build(),
                   EntityTestHelper.entityPropertyBuilder().name("p3").type(TEXT).constraints(List.of(new Length(10L))).build()
           )
        );

        var wrapper =  new EntityWrapper(entity, data, null, dataProviderService, 0);
        wrapper.createReferenced(new PropertyLink("p1", null));
        return wrapper;
    }

    @Test
    void testCall_DelayedProperties() {
        when(dataProviderService.getValueProviderByType("name")).thenReturn(new DefaultNameProvider());
        var countDownLatch = new CountDownLatch(1);
        var config = DataGenerateConfig.builder().build();
        var entity = TEST_HELPER.createEntityT6();
        var data = TEST_HELPER.createDataT6();
        var output = TEST_HELPER.createOutputControlT6();
        var wrapper =  new EntityWrapper(entity, data, output, dataProviderService, 0);
        wrapper.createReferenced(new PropertyLink("t6p0", null), new PropertyLink("t6p5", "t6p0"));
        var referenced = TEST_HELPER.createReferencedT6();
        var task = DataGenerateTask.builder()
                .countDownLatch(countDownLatch)
                .entityWrapper(wrapper)
                .config(config)
                .referenced(referenced)
                .build();
        var result = task.call();
        assertEquals(2, result.status());
        assertEquals(10, wrapper.getValues().size());
    }

    @Test
    void testCall_MaxCollision() {
        when(dataProviderService.getValueProviderByType("name")).thenReturn(new DefaultNameProvider());
        var countDownLatch = new CountDownLatch(1);
        var config = DataGenerateConfig.builder().maxIndexCollision(1).build();
        var entity = TEST_HELPER.createEntityT6();
        var data = TEST_HELPER.createDataT6();
        var output = TEST_HELPER.createOutputControlT6();
        var wrapper =  new EntityWrapper(entity, data, output, dataProviderService, 0);
        wrapper.createReferenced(new PropertyLink("t6p0", null), new PropertyLink("t6p5", "t6p0"));
        var referenced = TEST_HELPER.createReferencedT6();
        var task = DataGenerateTask.builder()
                .countDownLatch(countDownLatch)
                .entityWrapper(wrapper)
                .config(config)
                .referenced(referenced)
                .build();
        assertThrows(DataGenerateException.class, task::call);
    }

    @Test
    void testCall_CompoundValues() {
        when(dataProviderService.getValueProviderByType("name")).thenReturn(new DefaultIdProvider());
        when(dataProviderService.getValueProviderByType("id")).thenReturn(new DefaultIdProvider());
        var config = DataGenerateConfig.builder().build();
        var entity = TEST_HELPER.createCompoundValueEntity();
        var data = EntityTestHelper.entityDataBuilder().entity(entity.name()).count(10L).build();
        var wrapper = new EntityWrapper(entity, data, null, dataProviderService, 5);
        var countDownLatch = new CountDownLatch(1);
        var task = DataGenerateTask.builder()
                .countDownLatch(countDownLatch)
                .entityWrapper(wrapper)
                .config(config)
                .compoundConfig(Map.of("id", Map.of("value", "java.lang.String"), "name", Map.of("value", "java.lang.String")))
                .build();
        var result = task.call();
        assertEquals(2, result.status());
        assertEquals(10, wrapper.getValues().size());
        for (var row : wrapper.getValues()) {
            var name1 = String.valueOf(row.get(wrapper.getPropertyOrder("name1")));
            var name2 = String.valueOf(row.get(wrapper.getPropertyOrder("name2")));
            var name1Copy = String.valueOf(row.get(wrapper.getPropertyOrder("name1_copy")));
            var id1 = String.valueOf(row.get(wrapper.getPropertyOrder("id1")));
            var id2 = String.valueOf(row.get(wrapper.getPropertyOrder("id2")));
            assertEquals(name1, name1Copy);
            assertNotEquals(name1, name2);
            assertEquals(UUID.fromString(id1).toString(), id1);
            assertTrue(id2.matches("[0-9]+"));
        }
    }

    @Test
    void testCall_MultipleEntityReference() {
        var countDownLatch = new CountDownLatch(1);
        var config = DataGenerateConfig.builder().build();
        var data = new DataEntity("t5",
                Set.of(
                        entityPropertyBuilder().name("t5p0").index(entityIndexBuilder().build()).constraints(List.of(new Interval(1L, 0L))).build(),
                        entityPropertyBuilder().name("t5p1").index(entityIndexBuilder().id(2).build()).constraints(List.of(new Interval(1L, 0L)))
                                .reference(new Reference("t1", "t1p1", "t1p1"))
                                .build(),
                        entityPropertyBuilder().name("t5p2").index(entityIndexBuilder().id(2).build()).constraints(List.of(new Interval(1L, 0L)))
                                .reference(new Reference("t1", "t1p2", "t1p1")).build(),
                        entityPropertyBuilder().name("t5p3").type(TEXT).index(entityIndexBuilder().id(2).build())
                                .reference(new Reference("t2", "t2p1", null))
                                .build()
                )
        );
        var wrapper = new EntityWrapper(data, new EntityData("t5", 5L, null, null), null, dataProviderService, 0);
        Map<Reference, TypedValue> referenced = getReferenceTypedValueMap();
        var task = DataGenerateTask.builder()
                .countDownLatch(countDownLatch)
                .entityWrapper(wrapper)
                .config(config)
                .referenced(referenced)
                .build();
        var result = task.call();
        assertEquals(2, result.status());
        assertEquals(5, wrapper.getValues().size());
    }

    private static Map<Reference, TypedValue> getReferenceTypedValueMap() {
        SimpleTypedValue ref1 = new SimpleTypedValue(TEXT);
        LinkedTypedValue ref2 = new LinkedTypedValue(SEQUENCE, "t1p1");
        LinkedTypedValue ref3 = new LinkedTypedValue(SEQUENCE, "t1p1");
        for (long l = 1L; l < 6L; l++) {
            ref1.addValue("abc");
            ref2.addLinkedValue("t1p1", l);
            ref2.addLinkedValue("t1p2", l);
            ref3.addLinkedValue("t1p1", l);
            ref3.addLinkedValue("t1p2", l);
        }
        return Map.of(
                new Reference("t2", "t2p1", null), ref1,
                new Reference("t1", "t1p1", "t1p1"), ref2,
                new Reference("t1", "t1p2", "t1p1"), ref3
        );
    }
}
