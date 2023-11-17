package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElectedValueSelectorTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5})
    void testGenerate(int index) {
        Set<Integer> values = Set.of(1, 2, 3);
        IndexValueGenerator indexValueGenerator = mock(IndexValueGenerator.class);
        when(indexValueGenerator.generate()).thenReturn(index);
        var test = new ElectedValueSelector(values);
        for (int i = 0; i < 10; i++) {
            var rs = (Integer)test.generate();
            assertTrue(values.contains(rs));
        }
    }


}
