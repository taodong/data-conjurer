package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RangeLengthStringGeneratorTest {
    @Test
    void testGenerate() {
        var generator = new RangeLengthStringGenerator(10, 15);
        var length = generator.generate().length();
        assertTrue(length >= 10 && length < 15);
    }

    @ParameterizedTest
    @CsvSource({"0,10", "10,10", "10,9", "-1,5"})
    void testGenerate_InvalidLength(int min, int max) {
        assertThrows(IllegalArgumentException.class, () -> new RangeLengthStringGenerator(min, max));
    }
}
