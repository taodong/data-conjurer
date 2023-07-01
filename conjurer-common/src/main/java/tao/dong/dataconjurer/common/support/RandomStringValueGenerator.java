package tao.dong.dataconjurer.common.support;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.RandomStringGenerator;

import static org.apache.commons.text.CharacterPredicates.ASCII_ALPHA_NUMERALS;

public abstract sealed class RandomStringValueGenerator implements ValueGenerator<String> permits
        FixLengthStringGenerator, RangeLengthStringGenerator {

    protected static final CharacterPredicate[] DEFAULT_CHARSET = {ASCII_ALPHA_NUMERALS};
    protected final RandomStringGenerator generator;

    protected RandomStringValueGenerator(CharacterPredicate... characterPredicates) {
        this.generator = new RandomStringGenerator.Builder()
                .filteredBy(characterPredicates)
                .build();
    }
}
