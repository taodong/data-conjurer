package tao.dong.dataconjurer.common.support;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PastTimeAfterFunctionTest {
    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("PAST_TIME_AFTER", new PastTimeAfterFunction())
            );

    @Test
    void testEvaluate() throws EvaluationException, ParseException {
        var past = System.currentTimeMillis() - 1000;
        var epr = new Expression("PAST_TIME_AFTER(anchor)", EXPRESSION_CONFIGURATION);

        var current = System.currentTimeMillis();
        var rs = epr.with("anchor", past).evaluate().getNumberValue().longValue();
        assertTrue(past < rs && rs < current, "Failed to generate millisecond between anchor and now, past: " + past + " now: " + current + " generated: " + rs);
    }
}
