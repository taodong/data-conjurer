package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LengthTest {

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void testValidation(long length) {
        assertThrows(IllegalArgumentException.class, () -> new Length(length));
    }

    private static Stream<Arguments> testIsMet() {
        return Stream.of(
            Arguments.of(1L, 1L, true),
            Arguments.of(33L, 22L, false),
            Arguments.of(100L, 97L, false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsMet(long length, long val, boolean expected) {
        var l = new Length(length);
        assertEquals(expected, l.isMet(val));
    }
}
