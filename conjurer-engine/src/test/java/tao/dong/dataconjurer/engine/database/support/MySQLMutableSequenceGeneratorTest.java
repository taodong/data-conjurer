package tao.dong.dataconjurer.engine.database.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Interval;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySQLMutableSequenceGeneratorTest {

    private static Stream<Arguments> testConstructor() {
        return Stream.of(
                Arguments.of(Set.of(), 1L, 1L),
                Arguments.of(Set.of(new Interval(0L, -1L)), 1L, 1L),
                Arguments.of(Set.of(new Interval(2L, 5L)), 5L, 2L)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testConstructor(Set<Constraint<?>> constraints, Long expectedBase, Long expectedLeap) {
        var generator = new MySQLMutableSequenceGenerator(constraints);
        assertEquals(expectedBase, generator.getSequenceGenerator().getCurrent());
        assertEquals(expectedLeap, generator.getSequenceGenerator().getLeap());
    }
}
