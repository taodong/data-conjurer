package tao.dong.dataconjurer.common.i18n;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnicodeCharacterPredicateTest {
    private final UnicodeCharacterPredicate toTest = new UnicodeCharacterPredicate() {
        @Override
        public boolean test(int i) {
            // 100
            String hexMin = "0064";
            // 500
            String hexMax = "01F4";
            return isInUnicodeRange(hexMin, hexMax, i);
        }
    };

    private static Stream<Arguments> testIsInUnicodeRange() {
        return Stream.of(
                Arguments.of(100, true),
                Arguments.of(500, true),
                Arguments.of(315, true),
                Arguments.of(-1, false),
                Arguments.of(799, false)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsInUnicodeRange(int val, boolean expected) {
        assertEquals(expected, toTest.test(val));
    }
}
