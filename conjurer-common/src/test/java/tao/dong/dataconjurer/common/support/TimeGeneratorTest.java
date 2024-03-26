package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.NumberRange;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimeGeneratorTest {

    @Test
    void getDefaultGenerator() {
        var generator = new TimeGenerator(null);
        assertEquals(TimeGenerator.MINUS_839_HOURS, ((RandomLongGenerator)generator.getGenerator()).getMinInclusive());
        assertEquals(TimeGenerator.PLUS_839_HOURS, ((RandomLongGenerator)generator.getGenerator()).getMaxExclusive());
    }

    private static Stream<Arguments> testGenerate() {
        return Stream.of(
                Arguments.of(new NumberRange(TimeGenerator.MINUS_839_HOURS, TimeGenerator.PLUS_839_HOURS)),
                Arguments.of(new NumberRange(1L, 100L)),
                Arguments.of(new NumberRange(0L, 86400L))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGenerate(NumberRange range) {
        var generator = new TimeGenerator(Set.of(range));
        for (var i = 0; i < 10; i++) {
            var rs = generator.generate();
            assertTrue(range.getMin() <= rs && range.getMax() > rs, "Generated value " + rs + " isn't between " + range.getMin() + " and " + range.getMax());
        }
    }
}