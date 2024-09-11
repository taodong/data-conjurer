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

import static org.junit.jupiter.api.Assertions.*;

class StringTemplateFunctionTest {

    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("STRING_TEMPLATE", new StringTemplateFunction())
            );


    private static Stream<Arguments> evaluate() {
        return Stream.of(
                Arguments.of("hello${name}world", new String[]{"name", "new"}, "hellonewworld"),
                Arguments.of("Hello, ${name}!", new String[]{"name", "world"}, "Hello, world!"),
                Arguments.of("Hello, ${name}! How are you ${name}?", new String[]{"name", "world"}, "Hello, world! How are you world?"),
                Arguments.of("Hello, ${name}! How are you ${name1}?", new String[]{"name", "world", "name1", "today"}, "Hello, world! How are you today?"),
                Arguments.of("Hello, ${name}! How are you ${name1}?", new String[]{"name", "world", "name2", "today"}, "Hello, world! How are you ?"),
                Arguments.of("Hello, ${name}! How are you ${name1}?", new String[]{}, "Hello, ! How are you ?"),
                Arguments.of("Hello, ${name}! How are you $${name1}?", new String[]{"name", "world", "name1", "today"}, "Hello, world! How are you ${name1}?"),
                Arguments.of("Hello, ${name}! How are you $$${name1}?", new String[]{"name", "world", "name1", "today"}, "Hello, world! How are you $today?")
        );
    }

    @ParameterizedTest
    @MethodSource("evaluate")
    void evaluate(String template, String[] values, String expected) throws EvaluationException, ParseException {
        var expr = new Expression("STRING_TEMPLATE(template, values)", EXPRESSION_CONFIGURATION);
        var result = expr.with("template", template).with("values", values).evaluate().getStringValue();
        assertEquals(expected, result);
    }

}