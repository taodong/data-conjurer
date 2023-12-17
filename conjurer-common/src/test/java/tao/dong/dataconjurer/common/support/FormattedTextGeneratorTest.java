package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicate;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.CharacterGroup;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.Precision;
import tao.dong.dataconjurer.common.model.UnfixedSize;
import tao.dong.dataconjurer.common.service.CharacterGroupService;

import java.util.Set;

import static org.apache.commons.text.CharacterPredicates.ARABIC_NUMERALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FormattedTextGeneratorTest {

    @Test
    void testGenerateFixedLengthString() {
        Set<Constraint<?>> constraints = Set.of(new Length(10L));
        var generator = new FormattedTextGenerator(constraints, null);
        for(var i = 0; i < 10; i++) {
            var result = generator.generate();
            assertEquals(10, result.length(), "Expected generating 10 character string, but get " + result);
        }
    }

    @Test
    void testGenerateRandomLengthString() {
        Set<Constraint<?>> constraints = Set.of(new UnfixedSize(2L, 10L));
        var generator = new FormattedTextGenerator(constraints, null);
        for(var i = 0; i < 10; i++) {
            var length = generator.generate().length();
            assertTrue(length >= 2 && length <= 10, "Generated string length isn't in [2, 10] range. Length: " + length);
        }
    }

    @Test
    void testCreateDefaultGenerator() {
        Set<Constraint<?>> constraints = Set.of(new Precision(5));
        var generator = new FormattedTextGenerator(constraints, null);
        assertTrue(generator.getGenerator() instanceof RangeLengthStringGenerator);
        var internalGen = (RangeLengthStringGenerator)generator.generator;
        assertEquals(1, internalGen.getMinLength());
        assertEquals(100, internalGen.getMaxLength());
    }

    @Test
    void testGenerateWithCharGroup() {
         Set<Constraint<?>> constraints = Set.of(new CharacterGroup(Set.of("ea_hiragana", "ea_katakana")));
        var characterLookup = new CharacterGroupService();
        var generator = new FormattedTextGenerator(constraints, characterLookup);
        var output = generator.generate();
        assertFalse(StringUtils.isAsciiPrintable(output));
    }

    @Test
    void testGenerateWithCharGroup_Undefined() {
        var characterLookup = mock(CharacterGroupLookup.class);
        Set<Constraint<?>> constraints = Set.of(new UnfixedSize(2L, 5L));
        when(characterLookup.lookupCharacterGroups(anySet())).thenReturn(new CharacterPredicate[]{ARABIC_NUMERALS});
        var generator = new FormattedTextGenerator(constraints, characterLookup);
        assertTrue(generator.getGenerator() instanceof  RangeLengthStringGenerator);
        verify(characterLookup, times(1)).lookupCharacterGroups(anySet());
    }

}
