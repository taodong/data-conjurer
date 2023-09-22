package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalTest {
    private static Stream<Arguments> testIsMet() {
        return Stream.of(
                Arguments.of(5L, null, 10L, true),
                Arguments.of(3L, 7L, 10L, true),
                Arguments.of(3L, 7L, 6L, false),
                Arguments.of(3L, 7L, 9L, false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsMet(long leap, Long base, Long val, boolean expected) {
        var interval = new Interval(leap, base);
        assertEquals(expected, interval.isMet(val));
    }

}
