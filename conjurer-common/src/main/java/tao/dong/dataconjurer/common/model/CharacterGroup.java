package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CHAR_GROUP;

public record CharacterGroup(Set<String> groups) implements Constraint<String> {

    @JsonCreator
    public CharacterGroup(@JsonProperty("groups") @NotEmpty Set<String> groups) {
        this.groups = groups;
    }

    @Override
    public boolean isMet(String val) {
        return groups != null && groups.contains(val);
    }

    @Override
    public ConstraintType getType() {
        return CHAR_GROUP;
    }
}
