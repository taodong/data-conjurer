package tao.dong.dataconjurer.common.service;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicate;
import tao.dong.dataconjurer.common.i18n.characters.CharacterGroupLookup;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.text.CharacterPredicates.ARABIC_NUMERALS;
import static org.apache.commons.text.CharacterPredicates.ASCII_ALPHA_NUMERALS;
import static org.apache.commons.text.CharacterPredicates.ASCII_LETTERS;
import static org.apache.commons.text.CharacterPredicates.ASCII_LOWERCASE_LETTERS;
import static org.apache.commons.text.CharacterPredicates.ASCII_UPPERCASE_LETTERS;
import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;
import static tao.dong.dataconjurer.common.i18n.characters.EastAsiaCharacterPredicates.CJK_A;
import static tao.dong.dataconjurer.common.i18n.characters.EastAsiaCharacterPredicates.HIRAGANA;
import static tao.dong.dataconjurer.common.i18n.characters.EastAsiaCharacterPredicates.KATAKANA;

public class CharacterGroupService implements CharacterGroupLookup {
    private final static Map<String, CharacterPredicate> CHARACTER_PREDICATE_MAP = Map.ofEntries(
            Map.entry("ARABIC_NUMERALS", ARABIC_NUMERALS),
            Map.entry("ASCII_ALPHA_NUMERALS", ASCII_ALPHA_NUMERALS),
            Map.entry("ASCII_LETTERS", ASCII_LETTERS),
            Map.entry("ASCII_LOWERCASE_LETTERS", ASCII_LOWERCASE_LETTERS),
            Map.entry("ASCII_UPPERCASE_LETTERS", ASCII_UPPERCASE_LETTERS),
            Map.entry("DIGITS", DIGITS),
            Map.entry("LETTERS", LETTERS),
            Map.entry("EA_HIRAGANA", HIRAGANA),
            Map.entry("EA_KATAKANA", KATAKANA),
            Map.entry("EA_CJK_A", CJK_A)
    );

    public CharacterPredicate[] lookupCharacterGroups(Collection<String> names) {
        return lookupPredicates(DataHelper.streamNullableCollection(names).filter(Objects::nonNull).toArray(String[]::new));
    }

    @Override
    public CharacterPredicate lookup(@NotNull String name) {
        return CHARACTER_PREDICATE_MAP.get(StringUtils.upperCase(name));
    }

    @Override
    public CharacterPredicate getDefault() {
        return ASCII_ALPHA_NUMERALS;
    }
}
