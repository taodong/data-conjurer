package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.model.EntityWrapper;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.Reference;
import tao.dong.dataconjurer.common.model.TypedValue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class DataGenerateTaskTest {

    @Test
    void testCall_GenerateData() {
        var countDownLatch = new CountDownLatch(1);
        var config = DataGenerateConfig.builder().build();
        var data = new EntityData("t1", 10L);
        var wrapper = createTestEntityWrapper(data);
        TypedValue ref1 = new TypedValue(SEQUENCE);
        for (long l = 1L; l < 6L; l++) {
            ref1.addValue(l);
        }
        Map<Reference, TypedValue> referenced = Map.of(
                new Reference("t2", "p0"), ref1
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
        assertFalse(wrapper.getReferenced().get("p1").getValues().isEmpty());
    }

    private EntityWrapper createTestEntityWrapper(EntityData data) {
        var entity = new DataEntity("t1",
           Set.of(
                   new EntityProperty("p1", SEQUENCE, 1, List.of(new Interval(1L, 0L)), null),
                   new EntityProperty("p2", SEQUENCE, 0, null, new Reference("t2", "p0")),
                   new EntityProperty("p3", TEXT, 0, List.of(new Length(10L)), null)
           )
        );

        var wrapper =  new EntityWrapper(entity, data);
        wrapper.createReferenced("p1");
        return wrapper;
    }
}
