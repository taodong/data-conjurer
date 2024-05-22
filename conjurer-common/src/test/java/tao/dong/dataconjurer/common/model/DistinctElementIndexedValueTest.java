package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistinctElementIndexedValueTest {

    private static Stream<Arguments> testAddValue() {
        return Stream.of(
                Arguments.of(List.of(0, 1, 2, 6, "C"), true),
                Arguments.of(List.of(0, 1, 3, 6, "ED"), false),
                Arguments.of(List.of(1, 1, 3, 1, "B"), true),
                Arguments.of(List.of(1, 2, 1, 1, "A"), false),
                Arguments.of(List.of(0, 1, 1, 5, "X"), true),
                Arguments.of(Collections.emptyList(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("testAddValue")
    void testAddValue(List<Object> properties, boolean expected) {
        var index = new DistinctElementIndexedValue(new int[]{1, 2, 3}, new int[]{2, 3});
        index.addValue(List.of(0, 1, 3, 6, "B"));
        var rs = index.addValue(properties);
        assertEquals(expected, rs);
    }

    @Test
    void testAddValue_EmptyDistinctElements() {
        var index = new DistinctElementIndexedValue(new int[]{1, 2}, new int[]{});
        var rs = index.addValue(List.of(0, 1, 1, 2));
        assertTrue(rs);
    }

    @Test
    void testAddValue_NullDistinctElements() {
        var index = new DistinctElementIndexedValue(new int[]{1, 2}, null);
        var rs = index.addValue(List.of(0, 1, 1, 2));
        assertTrue(rs);
    }
}
