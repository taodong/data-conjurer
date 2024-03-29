package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicate;
import tao.dong.dataconjurer.common.model.CharacterGroup;
import tao.dong.dataconjurer.common.model.Constraint;
import tao.dong.dataconjurer.common.model.ConstraintType;
import tao.dong.dataconjurer.common.model.Length;
import tao.dong.dataconjurer.common.model.UnfixedSize;

import java.util.Collections;
import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CHAR_GROUP;
import static tao.dong.dataconjurer.common.model.ConstraintType.LENGTH;
import static tao.dong.dataconjurer.common.model.ConstraintType.SIZE;

public class FormattedTextGenerator extends StringValueGeneratorDecorator {
    private static final Set<ConstraintType> CONSTRAINT_TYPES  = Set.of(LENGTH, SIZE, CHAR_GROUP);

    public FormattedTextGenerator(Set<Constraint<?>> constraints, CharacterGroupLookup characterGroupLookup) {
        super(constraints, characterGroupLookup);
    }

    @Override
    protected ValueGenerator<String> getDefaultGenerator() {
        return new RangeLengthStringGenerator(1, 100);
    }

    @Override
    protected ValueGenerator<String> createGenerator(Set<Constraint<?>> constraints) {
        CharacterPredicate[] charGroups = null;
        int min = 1;
        int max = 100;
        for (var constraint : constraints) {
            if (LENGTH ==constraint.getType()) {
                var length = Math.toIntExact(((Length)constraint).getMax());
                min = length;
                max = length;
            } else if (SIZE == constraint.getType()) {
                min = Math.toIntExact(((UnfixedSize)constraint).getMin());
                max = Math.toIntExact(((UnfixedSize)constraint).getMax());
            } else if (CHAR_GROUP == constraint.getType() && characterGroupLookup != null) {
                charGroups = characterGroupLookup.lookupCharacterGroups(((CharacterGroup)constraint).groups());
            }
        }

        return createStringGenerator(min, max, charGroups);
    }

    private ValueGenerator<String> createStringGenerator(int min, int max, CharacterPredicate[] charGroups) {
        if (charGroups == null && characterGroupLookup != null) {
            charGroups = characterGroupLookup.lookupCharacterGroups(Collections.emptySet());
        }
        return min == max ? new FixLengthStringGenerator(max, charGroups) : new RangeLengthStringGenerator(min, max, charGroups);
    }
    
    @Override
    protected Set<Constraint<?>> filterConstraints(Set<Constraint<?>> constraints) {
        return FILTER_CONSTRAINTS.apply(constraints, CONSTRAINT_TYPES);
    }

    @Override
    protected void testConstraints(String val) {
        testConstraints(val, constraint -> switch (constraint.getType()) {
            case SIZE -> !((UnfixedSize)constraint).isMet((long) StringUtils.length(val));
            case LENGTH -> !((Length)constraint).isMet((long) StringUtils.length(val));
            default -> false;
        });
    }
}
