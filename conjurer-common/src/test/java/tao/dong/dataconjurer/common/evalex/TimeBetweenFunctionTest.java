package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeBetweenFunctionTest {

    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("TIME_BETWEEN", new TimeBetweenFunction())
            );

    private static Stream<Arguments> testEvaluate() {
        return Stream.of(
                Arguments.of(new BigDecimal(0), new BigDecimal(1000)),
                Arguments.of(new BigDecimal(1000), new BigDecimal(2000)),
                Arguments.of(new BigDecimal(2000), new BigDecimal(3000))
        );
    }

    @ParameterizedTest
    @MethodSource("testEvaluate")
    void testEvaluate(BigDecimal start, BigDecimal end) throws EvaluationException, ParseException {
        var epr = new Expression("TIME_BETWEEN(start, end)", EXPRESSION_CONFIGURATION);
        var result = epr.with("start", start).with("end", end).evaluate().getNumberValue().longValue();
        assertTrue(result >= start.longValue() && result <= end.longValue());
    }

    @Test
    void testEvaluate_InvalidRange() {
        assertThrows(EvaluationException.class, () -> {
            var epr = new Expression("TIME_BETWEEN(1000, 0)", EXPRESSION_CONFIGURATION);
            epr.evaluate().getNumberValue().longValue();
        });
    }
}