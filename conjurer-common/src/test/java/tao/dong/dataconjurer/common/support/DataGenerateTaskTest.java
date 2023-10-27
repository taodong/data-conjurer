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
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class DataGenerateTaskTest {

    @Test
    void testCall_GenerateData() {
        var countDownLatch = new CountDownLatch(1);
        var config = DataGenerateConfig.builder().build();
        var data = new EntityData("t1", 10L);
        var wrapper = createTestEntityWrapper(data);
//        Map<String, List<TypedValue>> references = Map.of(
//                "p0", List.of(1L, 2L, 3L, 4L, 5L)
//        );
        var task = DataGenerateTask.builder()
                .countDownLatch(countDownLatch)
                .entityWrapper(wrapper)
                .config(config)
                .build();
        var result = task.call();
        assertEquals(10, result.getValues().size());
    }

    private EntityWrapper createTestEntityWrapper(EntityData data) {
        var entity = new DataEntity("t1",
           Set.of(
                   new EntityProperty("p1", SEQUENCE, true, 1, List.of(new Interval(1L, 0L)), null),
                   new EntityProperty("p2", SEQUENCE, true, -1, null, new Reference("t2", "p0")),
                   new EntityProperty("p3", TEXT, true, -1, List.of(new Length(10L)), null)
           )
        );
        var wrapper = new EntityWrapper(entity, data);
        return wrapper;
    }
}
