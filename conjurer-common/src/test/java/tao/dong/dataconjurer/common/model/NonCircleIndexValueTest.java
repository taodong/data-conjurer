package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NonCircleIndexValueTest {

    private static Stream<Arguments> testAddValue() {
        return Stream.of(
                Arguments.of(List.of("ABC"), false),
                Arguments.of(List.of("XX", "B", "C", "D", "X"), true),
                Arguments.of(List.of("A", "B", "C", "D", "E"), false),
                Arguments.of(Arrays.asList(null, 3, "ABC", "1", 2), false),
                Arguments.of(List.of("E", "F", "K", "E", "D"), false),
                Arguments.of(List.of("E", "F", "K", "A", "D"), false),
                Arguments.of(List.of("V", "B", "C", "E", "D"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("testAddValue")
    void testAddValue(List<Object> entry, boolean expected) {
        UniqueIndex<?> indexes = new NonCircleIndexValue(new int[] {0, 3, 4}, 3, 4, false);
        indexes.addValue(List.of("A", "SX", "BI", "D", "E"));
        indexes.addValue(Arrays.asList("[_NULL_]", "XX", "YY", "1", "2"));
        assertEquals(expected, indexes.addValue(entry));
    }

    @Test
    void testAddValue_SingleParent() {
        UniqueIndex<?> indexes = new NonCircleIndexValue(new int[] {0, 1}, 0, 1, false);
        indexes.addValue(List.of(50,66,3));
        assertFalse(indexes.addValue(List.of(25, 66, 33)));
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "-1, 0", "0, -1"})
    void testValidate(int parentIndex, int childIndex) {
        assertThrows(IllegalArgumentException.class, () -> new NonCircleIndexValue(new int[]{0, 1, 2}, parentIndex, childIndex, false));
    }

    private static Stream<Arguments> testAddValue_OrderedIndex() {
        return Stream.of(
                Arguments.of(new ArrayList<>(Arrays.asList(12L, 7L, 8L, 9L)), 7L, 8L),
                Arguments.of(new ArrayList<>(Arrays.asList(5L, 5L, 4L, "abc", 6.0D, 300)), 4L, 5L),
                Arguments.of(new ArrayList<>(Arrays.asList("abc", null, "efg")), null, "efg"),
                Arguments.of(new ArrayList<>(Arrays.asList("sxd", 1L, "eff")), 1L, "eff")
        );
    }

    @ParameterizedTest
    @MethodSource("testAddValue_OrderedIndex")
    void testAddValue_OrderedIndex(List<Object> entry, Object parent, Object child) {
        var index = new NonCircleIndexValue(new int[] {1, 2}, 1, 2, true);
        index.addValue(entry);
        assertEquals(parent, entry.get(1));
        assertEquals(child, entry.get(2));
    }
}
