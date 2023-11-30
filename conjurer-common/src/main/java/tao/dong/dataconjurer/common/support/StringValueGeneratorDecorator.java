package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.model.Constraint;

import java.util.Set;

public abstract class StringValueGeneratorDecorator extends ValueGeneratorDecorator<String> {

    protected final CharacterGroupLookup characterGroupLookup;
    @Getter(AccessLevel.PACKAGE)
    protected final ValueGenerator<String> generator;

    protected StringValueGeneratorDecorator(Set<Constraint<?>> constraints, CharacterGroupLookup characterGroupLookup) {
        super((ValueGenerator<String>) null);
        this.characterGroupLookup = characterGroupLookup;
        var qualified = filterConstraints(constraints);
        if (!qualified.isEmpty()) {
            generator = createGenerator(qualified);
        } else {
            generator = getDefaultGenerator();
        }
    }

    @Override
    public String generate() {
        return this.generator.generate();
    }
}
