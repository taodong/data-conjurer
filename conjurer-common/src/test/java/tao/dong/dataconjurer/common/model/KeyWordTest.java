package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class KeyWordTest {

    private static Stream<Arguments> getMatcher() {
        return Stream.of(
                Arguments.of("<?null?>", true),
                Arguments.of("<?NULL?>", true),
                Arguments.of("<?Null?>", true),
                Arguments.of("<?NULL?> ", true),
                Arguments.of(" <?Null?>", true),
                Arguments.of(" Null ", false)
        );
    }

    // test the matcher function
    @ParameterizedTest
    @MethodSource("getMatcher")
    void getMatcher(String input, boolean expected) {
        KeyWord keyWord = KeyWord.NULL_KEY;
        assertEquals(expected, keyWord.getMatcher().apply(input));
    }

    // test the keyString function
    @Test
    void getKeyString() {
        KeyWord keyWord = KeyWord.NULL_KEY;
        assertEquals("<?null?>", keyWord.getKeyString());
    }

    // Provider and tests for CURRENT_DATETIME_KEY (timestamp-like)
    private static Stream<Arguments> getCurrentDatetimeMatcher() {
        return Stream.of(
                Arguments.of("<?current_datetime?>", true),
                Arguments.of("<?CURRENT_DATETIME?>", true),
                Arguments.of(" <?Current_Datetime?> ", true),
                Arguments.of("<?current_datetime?> ", true),
                Arguments.of("current_datetime", false),
                Arguments.of("<?current_date?>", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getCurrentDatetimeMatcher")
    void currentDatetimeMatcher(String input, boolean expected) {
        KeyWord keyWord = KeyWord.CURRENT_DATETIME_KEY;
        assertEquals(expected, keyWord.getMatcher().apply(input));
    }

    @Test
    void getCurrentDatetimeKeyString() {
        KeyWord keyWord = KeyWord.CURRENT_DATETIME_KEY;
        assertEquals("<?current_datetime?>", keyWord.getKeyString());
    }

    // Provider and tests for CURRENT_DATE_KEY
    private static Stream<Arguments> getCurrentDateMatcher() {
        return Stream.of(
                Arguments.of("<?current_date?>", true),
                Arguments.of("<?CURRENT_DATE?>", true),
                Arguments.of(" <?Current_Date?> ", true),
                Arguments.of("<?current_date?> ", true),
                Arguments.of("current_date", false),
                Arguments.of("<?current_datetime?>", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getCurrentDateMatcher")
    void currentDateMatcher(String input, boolean expected) {
        KeyWord keyWord = KeyWord.CURRENT_DATE_KEY;
        assertEquals(expected, keyWord.getMatcher().apply(input));
    }

    @Test
    void getCurrentDateKeyString() {
        KeyWord keyWord = KeyWord.CURRENT_DATE_KEY;
        assertEquals("<?current_date?>", keyWord.getKeyString());
    }
}