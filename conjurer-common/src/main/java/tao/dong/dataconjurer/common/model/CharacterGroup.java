package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Set;

import static tao.dong.dataconjurer.common.model.ConstraintType.CHAR_GROUP;

@JsonTypeName("char_group")
public record CharacterGroup(Set<String> groups) implements Constraint<String> {

    @Override
    public boolean isMet(String val) {
        return val != null && groups != null && groups.contains(val);
    }

    @Override
    public ConstraintType getType() {
        return CHAR_GROUP;
    }
}
