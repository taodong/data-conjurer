package tao.dong.dataconjurer.common.support;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainedValueGeneratorTest {

    @ParameterizedTest
    @CsvSource({"0, 1, 1", "1, 0, 1", "2, 0, 2"})
    void testGenerateChangeRatio(int style, double min, double max) {
        ChainedValueGenerator<Long> generator = new ChainedValueGenerator<>(0, 1, style, 1L) {
            @Override
            protected Long getNextValue() {
                return null;
            }
        };
        var rs = generator.generateChangeRatio();
        assertTrue(rs >= min && rs <= max);
    }


    @ParameterizedTest
    @CsvSource({"0, 1", "1, 1", "-1, 1"})
    void testGenerateLeap(int direction, double rs1) {
        ChainedValueGenerator<Long> generator = new ChainedValueGenerator<>(direction, 1, 0, 1L) {
            @Override
            protected Long getNextValue() {
                return null;
            }
        };

        var rs = generator.generateLeap();
        assertEquals(rs1, Math.abs(rs));

    }
}
