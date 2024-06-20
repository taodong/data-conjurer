package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.evalex.TimeAfterFunction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeAfterFunctionTest {
    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("TIME_AFTER", new TimeAfterFunction())
            );


    @Test
    void testEvaluate() throws EvaluationException, ParseException {
        var now = System.currentTimeMillis();
        var epr = new Expression("TIME_AFTER(now)", EXPRESSION_CONFIGURATION);

        var rs = epr.with("now", now).evaluate().getNumberValue().longValue();
        assertTrue(rs > now);

    }
}
