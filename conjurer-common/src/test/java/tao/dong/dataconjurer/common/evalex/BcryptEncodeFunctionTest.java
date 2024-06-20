package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tao.dong.dataconjurer.common.evalex.BcryptEncodeFunction;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BcryptEncodeFunctionTest {
    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("BCRYPT", new BcryptEncodeFunction())
            );


    private static Stream<Arguments> testEvaluate() {
        return Stream.of(
                Arguments.of("password"),
                Arguments.of("fafdsf*s")
        );
    }

    @ParameterizedTest
    @MethodSource("testEvaluate")
    void testEvaluate(String rawPassword) throws EvaluationException, ParseException {
        var epr = new Expression("BCRYPT(rawPassword)", EXPRESSION_CONFIGURATION);
        var encoder = new BCryptPasswordEncoder();
        var result = epr.with("rawPassword", rawPassword).evaluate().getStringValue();
        assertTrue(encoder.matches(rawPassword, result), "Failed to encode password with default strength");
    }
}