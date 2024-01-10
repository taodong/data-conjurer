package tao.dong.dataconjurer.engine.database.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySQLTextValueTest {

    private static Stream<Arguments> testGet() {
        return Stream.of(
                Arguments.of("abc", "'abc'"),
                Arguments.of("", "''"),
                Arguments.of("Jeramy O'Connell", "'Jeramy O\\'Connell'")
        );
    }
    @ParameterizedTest
    @MethodSource
    void testGet(String input, String expected) {
        assertEquals(expected, new MySQLTextValue(input).get());
    }
}
