package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixLengthStringGeneratorTest {

    @Test
    void testGenerate() {
        var generator = new FixLengthStringGenerator(10);
        assertEquals(10, generator.generate().length());
    }

    @Test
    void testGenerate_InvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> new FixLengthStringGenerator(0));
    }
}
