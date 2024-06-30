package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.RatioRange;
import tao.dong.dataconjurer.common.model.WeightedValue;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

class WeightedValueGeneratorTest {

    @Test
    void testGenerate() {
        var fallbackGen = mock(ValueGenerator.class);
        when(fallbackGen.generate()).thenReturn(10L);
        Set<Long> vals1 = Set.of(1L, 2L);
        Set<Long> vals2 = Set.of(3L);
        List<WeightedValue> values = List.of(
                new WeightedValue(new ElectedValueSelector(vals1), new RatioRange(0D, 0.5)),
                new WeightedValue(new ElectedValueSelector(vals2), new RatioRange(0.5, 0.75))
        );
        var test = new WeightedValueGenerator(SEQUENCE, values, fallbackGen);
        for (var i = 0; i < 20; i++) {
            var rs = (Long) test.generate();
            assertTrue(rs == 1L || rs == 2L || rs == 3L || rs == 10L, "Generated value " + rs + " isn't among expected values of 1, 2, 3 or 10");
        }
    }
}
