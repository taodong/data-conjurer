package tao.dong.dataconjurer.common.support;

import org.apache.commons.text.CharacterPredicate;

import static org.apache.commons.text.CharacterPredicates.ASCII_ALPHA_NUMERALS;

public class FixLengthStringGenerator extends RandomStringValueGenerator{

    private final int length;

    public FixLengthStringGenerator(int length) {
        this(length, ASCII_ALPHA_NUMERALS);
    }

    public FixLengthStringGenerator(int length, CharacterPredicate... characterPredicates) {
        super(characterPredicates);
        this.length = length;
    }

    @Override
    public String generate() {
        return null;
    }
}
