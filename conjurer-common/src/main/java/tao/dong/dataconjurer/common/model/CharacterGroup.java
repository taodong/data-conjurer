package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class CharacterGroup implements Constraint<String> {

    private final Set<String> characterGroups;

    @JsonCreator
    public CharacterGroup(@JsonProperty("groups") Set<String> characterGroups) {
        this.characterGroups = characterGroups;
    }

    @Override
    public boolean isMet(String val) {
        return characterGroups != null && characterGroups.contains(val);
    }

    @Override
    public ConstraintType getType() {
        return null;
    }
}
