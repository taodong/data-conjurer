package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringCaseFunctionTest {

    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("STRING_CASE", new StringCaseFunction())
            );

    private static Stream<Arguments> evaluate() {
        return Stream.of(
                Arguments.of("hello", "upper", "HELLO"),
                Arguments.of("HELLO", "lower", "hello"),
                Arguments.of("Hello", "LOWER", "hello"),
                Arguments.of("Hello", "UPper", "HELLO"),
                Arguments.of("Hello", "unknown", "Hello"),
                Arguments.of("hello", "pascal", "Hello"),
                Arguments.of("HELLO world 2 yOu", "pascal", "Hello World 2 You"),
                Arguments.of("hello world", "title", "Hello world"),
                Arguments.of("HELLO WORLD", "title", "Hello world")
        );
    }

    @ParameterizedTest
    @MethodSource("evaluate")
    void evaluate(String input, String caseType, String expected) throws EvaluationException, ParseException {
        var expr = new Expression("STRING_CASE(input, caseType)", EXPRESSION_CONFIGURATION);
        var result = expr.with("input", input).with("caseType", caseType).evaluate().getStringValue();
        assertEquals(expected, result);
    }
}