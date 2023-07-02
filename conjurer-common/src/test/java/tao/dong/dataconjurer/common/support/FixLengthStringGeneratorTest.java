package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixLengthStringGeneratorTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 25, 33})
    void testGenerate(int length) {
        var generator = new FixLengthStringGenerator(length);
        assertEquals(length, generator.generate().length());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void testGenerate_InvalidLength(int length) {
        assertThrows(IllegalArgumentException.class, () -> new FixLengthStringGenerator(length));
    }
}
