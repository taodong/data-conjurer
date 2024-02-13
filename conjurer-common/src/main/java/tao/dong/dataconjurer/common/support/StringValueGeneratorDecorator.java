package tao.dong.dataconjurer.common.support;

import lombok.AccessLevel;
import lombok.Getter;
import tao.dong.dataconjurer.common.model.Constraint;

import java.util.Set;

public abstract class StringValueGeneratorDecorator extends ValueGeneratorDecorator<String> {

    protected final CharacterGroupLookup characterGroupLookup;
    @Getter(AccessLevel.PACKAGE)
    protected final ValueGenerator<String> strGenerator;

    protected StringValueGeneratorDecorator(Set<Constraint<?>> constraints, CharacterGroupLookup characterGroupLookup) {
        super((ValueGenerator<String>) null);
        this.characterGroupLookup = characterGroupLookup;
        strGenerator = createGeneratorByConstraints(constraints);
    }

    @Override
    public String generate() {
        return strGenerator.generate();
    }
}
