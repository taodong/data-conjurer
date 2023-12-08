package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberCalculatorTest {

    private static Stream<Arguments> testCalculate() {
        return Stream.of(
                Arguments.of("p1 + p2", Set.of("p1", "p2"), Map.of("p1", new BigDecimal(1), "p2", new BigDecimal(2)), new BigDecimal(3)),
                Arguments.of("(log10(p1) + p2) / p1", Set.of("p1", "p2"),
                        Map.of("p1", new BigDecimal(10), "p2", new BigDecimal(2), "p3", new BigDecimal(3)), new BigDecimal("0.3"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCalculate(String formula, Set<String> params, Map<String, Object> inputs, BigDecimal expected) {
        var calculator = new NumberCalculator(formula, params);
        var result = calculator.calculate(inputs);
        assertEquals(expected, result);
    }

    private static Stream<Arguments> testExceptions() {
        return Stream.of(
                Arguments.of("p1 + p2", Set.of("p1", "p3"), Map.of("p1", new BigDecimal(1), "p2", new BigDecimal(2))),
                Arguments.of("abc", Set.of("p1", "p2"), Map.of("p1", new BigDecimal(1), "p2", new BigDecimal(2)))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testExceptions(String formula, Set<String> params, Map<String, Object> inputs) {
        var calculator = new NumberCalculator(formula, params);
        assertThrows(DataGenerateException.class, () -> calculator.calculate(inputs));
    }

    @Test
    void testGenerate() {
        var test = new NumberCalculator("p1 + p2", Set.of("p1", "p2"));
        assertThrows(UnsupportedOperationException.class, test::generate);
    }
}
