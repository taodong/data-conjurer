package tao.dong.dataconjurer.common.support;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tao.dong.dataconjurer.common.evalex.EvalExOperation;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringTransformerTest {

    private static Stream<Arguments> testCalculate() {
        return Stream.of(
                Arguments.of(new StringTransformer("p1 + p2", Set.of("p1", "p2")), Map.of("p1", "a", "p2", "b"), "ab"),
                Arguments.of(new StringTransformer("p1 + p2 + p3", Set.of("p1", "p2", "p3")), Map.of("p1", "a", "p2", "b", "p3", "c"), "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("testCalculate")
    void testCalculate(StringTransformer transformer, Map<String, Object> values, String expected) {
        var result = transformer.calculate(values);
        assertEquals(expected, result);
    }

    @Test
    void testCalculate_Bcrypt() {
        var test = new StringTransformer("BCRYPT(p1)", Set.of("p1"));
        var encoder = new BCryptPasswordEncoder();
        var result = test.calculate(Map.of("p1", "password"));
        assertTrue(encoder.matches("password", result));
    }

    @Test
    void testGenerate() {
        var test = new StringTransformer("p1 + p2", Set.of("p1", "p2"));
        assertThrows(UnsupportedOperationException.class, test::generate);
    }

    @Test
    void testGetOperationType() {
        var test = new StringTransformer("p1 + p2", Set.of("p1", "p2"));
        assertEquals(EvalExOperation.OperationType.STRING, test.getOperationType());
    }

    @Test
    void testCalculate_MissParameters() {
        var test = new StringTransformer("p1 + p2", Set.of("p1", "p2"));
        var ex = assertThrows(DataGenerateException.class, () -> test.calculate(Map.of("p1", "a")));
        assertEquals("Missing variable values for p1 + p2", ex.getMessage());
    }

    @Test
    void testCalculate_InvalidOperator() {
        var test = new StringTransformer("p1 - p2", Set.of("p1", "p2"));
        var ex = assertThrows(DataGenerateException.class, () -> test.calculate(Map.of("p1", "a", "p2", 1)));
        assertEquals("Failed to calculate value for p1 - p2", ex.getMessage());
    }
}