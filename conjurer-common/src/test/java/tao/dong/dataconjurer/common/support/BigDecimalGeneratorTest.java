package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.ChainedValue;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.NumberRange;
import tao.dong.dataconjurer.common.model.Precision;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BigDecimalGeneratorTest {

    private static Stream<Arguments> testConstruct() {
        return Stream.of(
                Arguments.of(Collections.emptySet(), Long.MIN_VALUE, Long.MAX_VALUE, 0),
                Arguments.of(Set.of(new Length(100L)), Long.MIN_VALUE, Long.MAX_VALUE, 0),
                Arguments.of(Set.of(new NumberRange(2L, 100L), new Precision(2)), 2L, 100L, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("testConstruct")
    void testConstruct(Set<Constraint<?>> constraints, Long min, Long max, int precision) {
        var decorator = new BigDecimalGenerator(constraints);
        assertEquals(min, ((RandomNumberGenerator)decorator.getGenerator()).getMinInclusive());
        assertEquals(max, ((RandomNumberGenerator)decorator.getGenerator()).getMaxExclusive());
        assertEquals(precision, ((RandomNumberGenerator)decorator.getGenerator()).getPrecision());
    }

    private static Stream<Arguments> testConstruct_ChainedBigDecimal() {
        return Stream.of(
                Arguments.of(Set.of(new ChainedValue(1.0, 0, 0), new NumberRange(2L, 100L))),
                Arguments.of(Set.of(new ChainedValue(1.0, 1, 1), new NumberRange(2L, 100L))),
                Arguments.of(Set.of(new ChainedValue(2.0, -1, 2), new NumberRange(5L, 80L)))
        );
    }

    @ParameterizedTest
    @MethodSource("testConstruct_ChainedBigDecimal")
    void testConstruct_ChainedBigDecimal(Set<Constraint<?>> constraints) {
        var decorator = new BigDecimalGenerator(constraints);
        assertTrue(decorator.getGenerator() instanceof ChainedBigDecimalGenerator);
    }

    private static Stream<Arguments> testTestConstraints() {
        return Stream.of(
                Arguments.of(new BigDecimal("5")),
                Arguments.of(new BigDecimal("79.99")),
                Arguments.of(new BigDecimal("5.05"))
        );
    }

    @ParameterizedTest
    @MethodSource("testTestConstraints")
    void testTestConstraints(BigDecimal val) {
        var decorator = new BigDecimalGenerator(
          Set.of(new NumberRange(5L, 80L), new Precision(2))
        );
        decorator.testConstraints(val);
        assertTrue(true);
    }

    private static Stream<Arguments> testTestConstraints_Failed() {
        return Stream.of(
                Arguments.of(new BigDecimal("4.99")),
                Arguments.of(new BigDecimal("80")),
                Arguments.of(new BigDecimal("33.005"))
        );
    }

    @ParameterizedTest
    @MethodSource("testTestConstraints_Failed")
    void testTestConstraints_Failed(BigDecimal val) {
        var decorator = new BigDecimalGenerator(
                Set.of(new NumberRange(5L, 80L), new Precision(2))
        );
        assertThrows(ConstraintViolationException.class, () -> decorator.testConstraints(val));
    }
}
