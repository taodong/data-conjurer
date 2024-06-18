package tao.dong.dataconjurer.common.support;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BcryptEncodeWithStrengthFunctionTest {
    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("BCRYPT2", new BcryptEncodeWithStrengthFunction())
            );

    private static Stream<Arguments> testEvaluate() {
        return Stream.of(
                Arguments.of("password", 0),
                Arguments.of("password", 4),
                Arguments.of("password", 10),
                Arguments.of("password", 15),
                Arguments.of("password", 32)
        );
    }

    @ParameterizedTest
    @MethodSource("testEvaluate")
    void testEvaluate(String rawPassword, int strength) throws EvaluationException, ParseException {
        var epr = new Expression("BCRYPT2(rawPassword, strength)", EXPRESSION_CONFIGURATION);
        if (strength <= 3 || strength > 10) {
            strength = -1;
        }
        var encoder = new BCryptPasswordEncoder(strength);
        var result = epr.with("rawPassword", rawPassword).with("strength", strength).evaluate().getStringValue();
        assertTrue(encoder.matches(rawPassword, result), "Failed to encode password with strength " + strength);
    }
}