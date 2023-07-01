package tao.dong.dataconjurer.common.support;

import org.apache.commons.text.CharacterPredicate;

public final class FixLengthStringGenerator extends RandomStringValueGenerator{

    private final int length;

    public FixLengthStringGenerator(int length) {
        this(length, DEFAULT_CHARSET);
    }

    public FixLengthStringGenerator(int length, CharacterPredicate... characterPredicates) {
        super(characterPredicates);
        this.length = length;
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid random text length: " + length);
        }
    }

    @Override
    public String generate() {
        return generator.generate(length);
    }
}
