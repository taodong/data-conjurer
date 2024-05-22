package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Interval;
import tao.dong.dataconjurer.common.model.Precision;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MutableSequenceGeneratorTest {

    private static Stream<Arguments> testConstructor() {
        return Stream.of(
                Arguments.of(null, 1L, 1L),
                Arguments.of(Set.of(new Precision(10)), 1L, 1L),
                Arguments.of(Set.of(new Interval(5L, 2L)), 2L, 5L)
        );
    }

    @ParameterizedTest
    @MethodSource("testConstructor")
    void testConstructor(Set<Constraint<?>> constraints, long expectedBase, long expectedLeap) {
        var generator = new MutableSequenceGenerator(constraints);
        var internalGen = generator.getSequenceGenerator();
        assertEquals(expectedBase, internalGen.getCurrent());
        assertEquals(expectedLeap, internalGen.getLeap());
    }

    @Test
    void testGenerate() {
        var generator = new MutableSequenceGenerator(null);
        var internalGen = generator.getSequenceGenerator();
        for (long i = 1; i < 5; i++) {
            assertEquals(i, internalGen.generate());
        }
    }
}
