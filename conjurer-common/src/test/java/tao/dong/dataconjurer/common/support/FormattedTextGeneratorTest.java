package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.Precision;
import tao.dong.dataconjurer.common.model.UnfixedSize;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormattedTextGeneratorTest {

    @Test
    void testGenerateFixedLengthString() {
        Set<Constraint<?>> constraints = Set.of(new Length(10L));
        var generator = new FormattedTextGenerator(constraints);
        for(var i = 0; i < 10; i++) {
            assertEquals(10, generator.generate().length());
        }
    }

    @Test
    void testGenerateRandomLengthString() {
        Set<Constraint<?>> constraints = Set.of(new UnfixedSize(2L, 10L));
        var generator = new FormattedTextGenerator(constraints);
        for(var i = 0; i < 10; i++) {
            var length = generator.generate().length();
            assertTrue(length >= 2 && length <= 10, "Generated string length isn't in [2, 10] range. Length: " + length);
        }
    }

    @Test
    void testCreateDefaultGenerator() {
        Set<Constraint<?>> constraints = Set.of(new Precision(5));
        var generator = new FormattedTextGenerator(constraints);
        assertTrue(generator.getGenerator() instanceof RangeLengthStringGenerator);
        var internalGen = (RangeLengthStringGenerator)generator.generator;
        assertEquals(1, internalGen.getMinLength());
        assertEquals(100, internalGen.getMaxLength());
    }
}
