package tao.dong.dataconjurer.common.i18n;

public enum EastAsiaCharacterPredicates implements UnicodeCharacterPredicate{

    HIRAGANA {
        @Override
        public boolean test(int i) {
            return isInUnicodeRange("3040", "309F", i);
        }
    },
    KATAKANA {
        @Override
        public boolean test(int i) {
            return isInUnicodeRange("31F0", "31FF", i);
        }
    },

    CJK_A {
        @Override
        public boolean test(int i) {
            return isInUnicodeRange("3400", "4DBF", i);
        }
    };
    EastAsiaCharacterPredicates() {}
}
