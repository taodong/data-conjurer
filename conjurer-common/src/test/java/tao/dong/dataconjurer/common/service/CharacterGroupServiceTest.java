package tao.dong.dataconjurer.common.service;

import org.apache.commons.text.CharacterPredicate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.text.CharacterPredicates.ARABIC_NUMERALS;
import static org.apache.commons.text.CharacterPredicates.ASCII_ALPHA_NUMERALS;
import static org.apache.commons.text.CharacterPredicates.ASCII_LETTERS;
import static org.apache.commons.text.CharacterPredicates.ASCII_LOWERCASE_LETTERS;
import static org.apache.commons.text.CharacterPredicates.ASCII_UPPERCASE_LETTERS;
import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.i18n.EastAsiaCharacterPredicates.CJK_A;
import static tao.dong.dataconjurer.common.i18n.EastAsiaCharacterPredicates.HIRAGANA;
import static tao.dong.dataconjurer.common.i18n.EastAsiaCharacterPredicates.KATAKANA;

class CharacterGroupServiceTest {

    private final CharacterGroupService service = new CharacterGroupService();

    private static Stream<Arguments> testLookupCharacterGroups() {
        return Stream.of(
                Arguments.of(null, 1, Set.of(ASCII_ALPHA_NUMERALS)),
                Arguments.of(Set.of("abc", "efg"), 1, Set.of(ASCII_ALPHA_NUMERALS)),
                Arguments.of(Set.of("ea_hiragana", "ascii_letters"), 2, Set.of(ASCII_LETTERS, HIRAGANA)),
                Arguments.of(
                Set.of("ARABIC_NUMERALS", "ASCII_ALPHA_NUMERALS", "ASCII_LETTERS", "ASCII_LOWERCASE_LETTERS", "ASCII_UPPERCASE_LETTERS", "DIGITS", "LETTERS", "EA_HIRAGANA", "EA_KATAKANA", "EA_CJK_A"),
                        10,
                        Set.of(ARABIC_NUMERALS, ASCII_ALPHA_NUMERALS, ASCII_LETTERS, ASCII_LOWERCASE_LETTERS, ASCII_UPPERCASE_LETTERS, DIGITS, LETTERS, HIRAGANA, KATAKANA, CJK_A))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testLookupCharacterGroups(Collection<String> names, int expected, Set<CharacterPredicate> expectedValues) {
        var result = service.lookupCharacterGroups(names);
        assertEquals(expected, result.length);
        for (var charset : result) {
            assertTrue(expectedValues.contains(charset));
        }
    }

}
