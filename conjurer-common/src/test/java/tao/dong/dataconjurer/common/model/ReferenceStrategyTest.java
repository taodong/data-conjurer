package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.ReferenceStrategy.LOOP;
import static tao.dong.dataconjurer.common.model.ReferenceStrategy.RANDOM;

class ReferenceStrategyTest {

    private static Stream<Arguments> testGetByName() {
        return Stream.of(
                Arguments.of("random", RANDOM),
                Arguments.of("loop", LOOP),
                Arguments.of("abc", RANDOM),
                Arguments.of(null, RANDOM)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetByName(String name, ReferenceStrategy expected) {
        assertEquals(expected, ReferenceStrategy.getByName(name));
    }
}
