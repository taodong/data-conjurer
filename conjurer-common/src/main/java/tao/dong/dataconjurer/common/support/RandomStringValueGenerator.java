package tao.dong.dataconjurer.common.support;

import org.apache.commons.text.CharacterPredicate;

public abstract class RandomStringValueGenerator implements ValueGenerator<String>{
    protected final CharacterPredicate[] characterPredicates;

    public RandomStringValueGenerator(CharacterPredicate... characterPredicates) {
        this.characterPredicates = characterPredicates;
    }
}
