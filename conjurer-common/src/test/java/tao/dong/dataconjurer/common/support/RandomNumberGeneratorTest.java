package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomNumberGeneratorTest {

    private static Stream<Arguments> testIsDoubleType() {
        return Stream.of(
                Arguments.of(0, false),
                Arguments.of(1, true),
                Arguments.of(2, true),
                Arguments.of(-1, false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsDoubleType(int precision, boolean expected) {
        var generator = RandomNumberGenerator.builder()
                .precision(precision)
                .build();
        assertEquals(expected, generator.isDoubleType());
    }

    @Test
    void testGenerate_Long() {
        var min = -78L;
        var max = 133L;
        var generator = RandomNumberGenerator.builder()
                .minInclusive(min)
                .maxExclusive(max)
                .build();
        var bottom = new BigDecimal(min);
        var top = new BigDecimal(max);
        for (var i = 0; i < 10; i++) {
            var result = generator.generate();
            assertTrue(bottom.compareTo(result) <= 0 && top.compareTo(result) > 0);
        }
    }

    @Test
    void testGenerate_Double() {
        var min = -503L;
        var max = 22L;
        var generator = RandomNumberGenerator.builder()
                .minInclusive(min)
                .maxExclusive(max)
                .precision(2)
                .build();
        var bottom = new BigDecimal(min);
        var top = new BigDecimal(max);
        for (var i = 0; i < 10; i++) {
            var result = generator.generate();
            assertTrue(bottom.compareTo(result) <= 0 && top.compareTo(result) > 0, "Generated value " + result + " isn't between -503L and 22L");
        }
    }
}
