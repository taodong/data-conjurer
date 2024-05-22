package tao.dong.dataconjurer.common.support;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomIndexGeneratorTest {

    private static Stream<Arguments> testGenerate() {
        return Stream.of(
                Arguments.of(new RandomIndexGenerator(10), 10),
                Arguments.of(new RandomIndexGenerator(1), 1),
                Arguments.of(new RandomIndexGenerator(372), 372)
        );
    }

    @ParameterizedTest
    @MethodSource("testGenerate")
    void testGenerate(RandomIndexGenerator generator, int size) {
        for (var i = 0; i < 10; i++) {
            assertTrue(generator.generate() < size);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -2, -41})
    void testInvalidConstructor(int size) {
        assertThrows(IllegalArgumentException.class, () -> new RandomIndexGenerator(size));
    }
}
