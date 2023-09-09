package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class TypedValueTest {
    private static Stream<Arguments> testAddValue() {
        return Stream.of(
                Arguments.of(new TypedValue(TEXT), new Object[]{"ABC", "ABC", "", "   ", "xfiso fdsfs"}, 4),
                Arguments.of(new TypedValue(SEQUENCE), new Object[]{1L, 2L, 100L, 2L}, 3)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testAddValue(TypedValue tv, Object[] values, int size) {
        for (var val : values) {
            tv.addValue(val);
        }
        assertEquals(size, tv.getValues().size());
    }

}
