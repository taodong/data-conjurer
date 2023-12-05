package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;
import static tao.dong.dataconjurer.common.model.TypedValue.DataType.SIMPLE;

class SimpleTypedValueTest {
    private static Stream<Arguments> testAddValue() {
        return Stream.of(
                Arguments.of(new SimpleTypedValue(TEXT), new Object[]{"ABC", "ABC", "", "   ", "xfiso fdsfs"}, 4),
                Arguments.of(new SimpleTypedValue(SEQUENCE), new Object[]{1L, 2L, 100L, 2L}, 3)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testAddValue(SimpleTypedValue tv, Object[] values, int size) {
        for (var val : values) {
            tv.addValue(val);
        }
        assertEquals(size, tv.getValues().size());
    }

    @Test
    void testAddValue_MismatchedType() {
        var typedValue = new SimpleTypedValue(TEXT);
        assertThrows(IllegalArgumentException.class, () -> typedValue.addValue(1));
    }

    @Test
    void testJoin() {
        var tv1 = new SimpleTypedValue(TEXT);
        tv1.addValue("A");
        tv1.addValue("B");
        var tv2 = new SimpleTypedValue(TEXT);
        tv2.addValue("B");
        tv2.addValue("C");
        tv1.join(tv2);
        assertEquals(3, tv1.getValues().size());
    }

    private static Stream<Arguments> testJoin_MismatchedType() {
        return Stream.of(
                Arguments.of(new SimpleTypedValue(SEQUENCE)),
                Arguments.of(new LinkedTypedValue(TEXT, "abc"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testJoin_MismatchedType(TypedValue tv2) {
        var tv1 = new SimpleTypedValue(TEXT);
        assertThrows(IllegalArgumentException.class, () -> tv1.join(tv2));
    }

    @Test
    void testGetOrderedValues() {
        var tv1 = new SimpleTypedValue(TEXT);
        tv1.addValue("A");
        assertEquals(1, tv1.getOrderedValues().size());
        tv1.addValue("B");
        tv1.clearOrderedValues();
        assertEquals(2, tv1.getOrderedValues().size());
    }

    @Test
    void testGetDataType() {
        var tv1 = new SimpleTypedValue(TEXT);
        assertEquals(SIMPLE, tv1.getDataType());
    }
}
