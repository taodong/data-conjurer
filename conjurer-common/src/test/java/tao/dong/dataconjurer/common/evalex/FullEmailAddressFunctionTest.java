package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FullEmailAddressFunctionTest {

    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("FULL_EMAIL_ADDRESS", new FullEmailAddressFunction())
            );

    private static Stream<Arguments> evaluate() {
        return Stream.of(
                Arguments.of(new String[]{"test@abc.com"}, "test<test@abc.com>"),
                Arguments.of(new String[]{"TEst@abc.com"}, "TEst<TEst@abc.com>"),
                Arguments.of(new String[]{"fdsfs@fdsfs.com", "FN LN"}, "FN LN<fdsfs@fdsfs.com>"),
                Arguments.of(new String[]{"fsdfsfsd"}, "fsdfsfsd"),
                Arguments.of(new String[]{"fsdfsfsd", "FN LN"}, "FN LN<fsdfsfsd>")
        );
    }

    @ParameterizedTest
    @MethodSource("evaluate")
    void evaluate(String[] input, String expected) throws EvaluationException, ParseException {
        var expr = new Expression("FULL_EMAIL_ADDRESS(input)", EXPRESSION_CONFIGURATION);
        var result = expr.with("input", input).evaluate().getStringValue();
        assertEquals(expected, result);
    }

    @Test
    void evaluate_ParamSizeMismatched() {
        var expr = new Expression("FULL_EMAIL_ADDRESS(input)", EXPRESSION_CONFIGURATION);
        assertThrows(EvaluationException.class, () -> expr.with("input", new String[]{}).evaluate());
        assertThrows(EvaluationException.class, () -> expr.with("input", new String[]{"a", "b", "c"}).evaluate());
    }

}