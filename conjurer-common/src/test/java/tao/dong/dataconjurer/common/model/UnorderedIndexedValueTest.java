package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnorderedIndexedValueTest {

    private static Stream<Arguments> testAddValue() {
        return Stream.of(
                Arguments.of(List.of("ABC"), false),
                Arguments.of(List.of("XX", "B", "C", "D", "E"), true),
                Arguments.of(List.of("A", "B", "C", "D", "E"), false),
                Arguments.of(Arrays.asList(null, 3, "ABC", "1", 2), false),
                Arguments.of(List.of("E", "F", "K", "E", "D"), false),
                Arguments.of(List.of("E", "F", "K", "A", "D"), false),
                Arguments.of(List.of("V", "B", "C", "E", "D"), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testAddValue(List<Object> entry, boolean expected) {
        UnorderedIndexedValue indexes = new UnorderedIndexedValue(new int[] {0, 3, 4});
        indexes.addValue(List.of("A", "SX", "BI", "D", "E"));
        indexes.addValue(Arrays.asList("[_NULL_]", "XX", "YY", "1", "2"));
        assertEquals(expected, indexes.addValue(entry));
    }

    @Test
    void testRemoveLastValue() {
        var indexes = new UnorderedIndexedValue(new int[]{1, 2});
        indexes.addValue(List.of("a", "b", "c", "d"));
        assertEquals(1, indexes.getValues().size());
        indexes.addValue(List.of("x", "y", "z", "a"));
        indexes.removeLastValue();
        assertEquals(1, indexes.getValues().size());
        assertEquals(Set.of("b", "c"), indexes.getValues().iterator().next());
    }

}
