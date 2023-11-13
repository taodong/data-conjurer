package tao.dong.dataconjurer.common.support;

import lombok.Getter;
import org.apache.commons.text.CharacterPredicate;

@Getter
public final class RangeLengthStringGenerator extends RandomStringValueGenerator{

    private final int minLength;
    private final int maxLength;

    public RangeLengthStringGenerator(int minLengthInclude, int maxLengthInclude) {
        this(minLengthInclude, maxLengthInclude, DEFAULT_CHARSET);
    }

    public RangeLengthStringGenerator(int minLengthInclude, int maxLengthExclude, CharacterPredicate... characterPredicates) {
        super(characterPredicates);
        this.minLength = minLengthInclude;
        this.maxLength = maxLengthExclude;
        if (minLengthInclude <= 0 || maxLengthExclude <= minLengthInclude) {
            throw new IllegalArgumentException("Invalid random text length: [" + minLengthInclude + " , " + maxLengthExclude + ")");
        }
    }

    @Override
    public String generate() {
        return generator.generate(minLength, maxLength);
    }
}
