package tao.dong.dataconjurer.common.i18n.characters;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class EastAsiaCharacterPredicatesTest {

    @ParameterizedTest
    @EnumSource(EastAsiaCharacterPredicates.class)
    void testEastAsiaCharacterGeneration(CharacterPredicate predicate) {
        var generator = new RandomStringGenerator.Builder().filteredBy(predicate).build();
        var rs = generator.generate(50);
        System.out.println("Generated " + predicate.toString() + " characters: " + rs);
        assertFalse(StringUtils.isAsciiPrintable(rs));
    }
}
