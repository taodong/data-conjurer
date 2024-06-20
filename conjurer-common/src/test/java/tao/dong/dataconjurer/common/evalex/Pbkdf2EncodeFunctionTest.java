package tao.dong.dataconjurer.common.evalex;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import tao.dong.dataconjurer.common.evalex.Pbkdf2EncodeFunction;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Pbkdf2EncodeFunctionTest {
    private static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.defaultConfiguration()
            .withAdditionalFunctions(
                    Map.entry("PBKDF2_SHA1", new Pbkdf2EncodeFunction())
            );


    private static Stream<Arguments> testEvaluate() {
        return Stream.of(
                Arguments.of("secret", 32, 10000, "password"),
                Arguments.of("secret1", 16, 65532, "password1")
        );
    }

    @ParameterizedTest
    @MethodSource("testEvaluate")
    void testEvaluate(String secret, int saltLength, int iterations, String rawPassword) throws EvaluationException, ParseException {
        var encoder = new Pbkdf2PasswordEncoder(secret, saltLength, iterations, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1);
        var expr = new Expression("PBKDF2_SHA1(secret, saltLength, iterations, rawPassword)", EXPRESSION_CONFIGURATION);
        var result = expr.with("secret", secret).with("saltLength", saltLength).with("iterations", iterations).with("rawPassword", rawPassword).evaluate().getStringValue();
        assertTrue(encoder.matches(rawPassword, result), "Failed to encode password.");
    }
}