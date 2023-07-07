package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SequenceGeneratorTest {


    private static Stream<Arguments> testGenerate() {
        return Stream.of(
                Arguments.of(1L, 1L, 1, 1L),
                Arguments.of(1L, 1L, 10,10L),
                Arguments.of(5L, 2L, 2, 7L),
                Arguments.of(3L, -4L, 3, -5L)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerate(long start, long leap, int counts, long expected) {
        var generator = new SequenceGenerator(start, leap);
        long rs = 0L;
        for (int i = 0; i < counts; i++)  {
            rs = generator.generate();
        }
        assertEquals(expected, rs);
    }

    @Test
    void testErrorOnZeroLeap() {
        assertThrows(IllegalArgumentException.class, () -> new SequenceGenerator(1L, 0L));
    }
}
