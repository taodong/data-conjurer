package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnfixedSizeTest {
    @ParameterizedTest
    @CsvSource({"-1, 1", "2, 2", "3, 2"})
    void testValidate(long min, long max) {
        assertThrows(IllegalArgumentException.class, () -> new UnfixedSize(min, max));
    }

    @Test
    void testValidate_NullValue() {
        assertThrows(IllegalArgumentException.class, () -> new UnfixedSize(null, null));
    }

    @ParameterizedTest
    @CsvSource({"1, true", "3, false"})
    void testIsMet(long val, boolean expected) {
        var test = new UnfixedSize(0L, 2L);
        assertEquals(expected, test.isMet(val));
    }
}
