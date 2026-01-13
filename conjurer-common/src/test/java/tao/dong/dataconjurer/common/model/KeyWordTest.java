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

    // Provider and tests for NOW
    private static Stream<Arguments> getNowMatcher() {
        return Stream.of(
                Arguments.of("<?now?>", true),
                Arguments.of("<?NOW?>", true),
                Arguments.of(" <?Now?> ", true),
                Arguments.of("<?now?> ", true),
                Arguments.of("now", false),
                Arguments.of("<?null?>", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getNowMatcher")
    void nowMatcher(String input, boolean expected) {
        KeyWord keyWord = KeyWord.NOW_KEY;
        assertEquals(expected, keyWord.getMatcher().apply(input));
    }

    @Test
    void getNowKeyString() {
        KeyWord keyWord = KeyWord.NOW_KEY;
        assertEquals("<?now?>", keyWord.getKeyString());
    }
}