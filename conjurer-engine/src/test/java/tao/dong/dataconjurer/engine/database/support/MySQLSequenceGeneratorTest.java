package tao.dong.dataconjurer.engine.database.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySQLSequenceGeneratorTest {

    private static Stream<Arguments> testInvalidSetting() {
        return Stream.of(
                Arguments.of(0L, 1L),
                Arguments.of(2L, 0L),
                Arguments.of(-1L, -1L)
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidSetting")
    void testInvalidSetting(Long current, Long leap) {
        assertThrows(IllegalArgumentException.class, () -> new MySQLSequenceGenerator(current, leap));
    }

    @Test
    void testValidSetting() {
        var generator = new MySQLSequenceGenerator(5L, 1L);
        assertEquals(5, generator.generate());
    }
}
