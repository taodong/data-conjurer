package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

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
    @MethodSource
    void testAddValue(List<Object> entry, boolean expected) {
        UniqueIndex<?> indexes = new NonCircleIndexValue(new int[] {0, 3, 4}, 3, 4);
        indexes.addValue(List.of("A", "SX", "BI", "D", "E"));
        indexes.addValue(Arrays.asList("[_NULL_]", "XX", "YY", "1", "2"));
        assertEquals(expected, indexes.addValue(entry));
    }

    @Test
    void testAddValue_SingleParent() {
        UniqueIndex<?> indexes = new NonCircleIndexValue(new int[] {0, 1}, 0, 1);
        indexes.addValue(List.of(50,66,3));
        assertFalse(indexes.addValue(List.of(25, 66, 33)));
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "-1, 0", "0, -1"})
    void testValidate(int parentIndex, int childIndex) {
        assertThrows(IllegalArgumentException.class, () -> new NonCircleIndexValue(new int[]{0, 1, 2}, parentIndex, childIndex));
    }
}
