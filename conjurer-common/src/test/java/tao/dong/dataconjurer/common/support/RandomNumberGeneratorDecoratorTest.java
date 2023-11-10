package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Length;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomNumberGeneratorDecoratorTest {

    private static Stream<Arguments> testConstruct() {
        return Stream.of(
                Arguments.of(Collections.emptySet(), Long.MIN_VALUE, Long.MAX_VALUE, 0),
                Arguments.of(Set.of(new Length(100L)), Long.MIN_VALUE, Long.MAX_VALUE, 0)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testConstruct(Set<Constraint<?>> constraints, Long min, Long max, int precision) {
        var decorator = new RandomNumberGeneratorDecorator(constraints);
        assertEquals(min, decorator.getGenerator().getMinInclusive());
        assertEquals(max, decorator.getGenerator().getMaxExclusive());
        assertEquals(precision, decorator.getGenerator().getPrecision());
    }
}
