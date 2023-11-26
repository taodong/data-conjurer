package tao.dong.dataconjurer.common.i18n.characters;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.text.CharacterPredicate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public interface CharacterGroupLookup {
    CharacterPredicate[] lookupCharacterGroups(Collection<String> names);

    CharacterPredicate lookup(@NotNull String name);

    CharacterPredicate getDefault();

    default CharacterPredicate[] lookupPredicates(String... names) {
        var result =  Arrays.stream(names).map(this::lookup).filter(Objects::nonNull).toArray(CharacterPredicate[]::new);
        return result.length > 0 ? result : new CharacterPredicate[]{getDefault()};
    }
}
