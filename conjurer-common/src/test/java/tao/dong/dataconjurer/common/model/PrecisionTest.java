package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrecisionTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 11, 100})
    void testValidation(int max) {
        assertThrows(IllegalArgumentException.class, ()-> new Precision(max));
    }

    private static Stream<Arguments> testIsMet() {
        return Stream.of(
                Arguments.of(0, 0, true),
                Arguments.of(1, 0, true),
                Arguments.of(1, 1, true),
                Arguments.of(10, 5, true),
                Arguments.of(10, 10, true),
                Arguments.of(10, 11, false),
                Arguments.of(10, -1, false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsMet(int max, int val, boolean expected) {
        var precision = new Precision(max);
        assertEquals(expected, precision.isMet(val));
    }
}
