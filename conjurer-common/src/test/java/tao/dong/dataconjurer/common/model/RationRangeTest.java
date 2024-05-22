package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tao.dong.dataconjurer.common.model.ConstraintType.RATIO_RANGE;

class RationRangeTest {

    private static Stream<Arguments> testValidation() {
        return Stream.of(
                Arguments.of(-0.3D, 0.5D),
                Arguments.of(0.1D, 1.1D),
                Arguments.of(-0.2D, 2.3D),
                Arguments.of(0.6D, 0.4D)
        );
    }

    @ParameterizedTest
    @MethodSource("testValidation")
    void testValidation(double min, double max) {
        assertThrows(IllegalArgumentException.class, () -> new RatioRange(min, max));
    }

    private static Stream<Arguments> testIsMet() {
        return Stream.of(
                Arguments.of(0D, 0.3D, 0.3D, true),
                Arguments.of(0.1D, 0.2D, 0.1D, false),
                Arguments.of(0.55D, 0.73D, 0.6D, true),
                Arguments.of(0.4D, 0.8D, 0.9D, false),
                Arguments.of(0.5D, 0.5D, 0.5D, false)
        );

    }

    @ParameterizedTest
    @MethodSource("testIsMet")
    void testIsMet(double min, double max, double val, boolean expected) {
        var ratioRange = new RatioRange(min, max);
        assertEquals(expected, ratioRange.isMet(val));
    }

    @Test
    void testGetType() {
        var ratioRange = new RatioRange(0.1, 0.2);
        assertEquals(RATIO_RANGE, ratioRange.getType());
    }
}
