package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.api.V1DataProviderApi;
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
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class DataGenerateTaskTest {
    private final V1DataProviderApi dataProviderApi = mock(V1DataProviderApi.class);

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

        var wrapper =  new EntityWrapper(entity, data, null, dataProviderApi);
        wrapper.createReferenced(new PropertyLink("p1", null));
        return wrapper;
    }
}
