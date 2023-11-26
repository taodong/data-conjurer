package tao.dong.dataconjurer.common.i18n;

import org.apache.commons.text.CharacterPredicate;

public interface UnicodeCharacterPredicate extends CharacterPredicate {

    default boolean isInUnicodeRange(String hexMin, String hexMax, int num) {
        return num >= Integer.parseInt(hexMin, 16) && num <= Integer.parseInt(hexMax, 16);
    }
}
