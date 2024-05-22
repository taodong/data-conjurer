package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoopIndexGeneratorTest {

    private static Stream<Arguments> testGenerate() {
        return Stream.of(
                Arguments.of(new LoopIndexGenerator(10), 1),
                Arguments.of(new LoopIndexGenerator(5, 2), 3),
                Arguments.of(new LoopIndexGenerator(3, 2), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testGenerate")
    void testGenerate(LoopIndexGenerator generator, int expected) {
        generator.generate();
        var rs = generator.generate();
        assertEquals(expected, rs);
    }


    @Test
    void testGenerate_round() {
        var generator = new LoopIndexGenerator(7);
        for (int i = 0; i < 20; i++) {
            var out = generator.generate();
            assertEquals(i % 7, out);
        }
    }

    private static Stream<Arguments> testInvalidConstructor() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(-1, 0),
                Arguments.of(3, -1),
                Arguments.of(8, 9),
                Arguments.of(7, 7),
                Arguments.of(-2, -3)
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidConstructor")
    void testInvalidConstructor(int size, int next) {
        assertThrows(IllegalArgumentException.class, () -> new LoopIndexGenerator(size, next));
    }
}
