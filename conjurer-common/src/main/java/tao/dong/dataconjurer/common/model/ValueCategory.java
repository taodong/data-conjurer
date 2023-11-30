package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotEmpty;

import java.util.Locale;

import static tao.dong.dataconjurer.common.model.ConstraintType.CATEGORY;

@JsonTypeName("category")
public record ValueCategory (@NotEmpty String name, String qualifier, Locale locale) implements Constraint<String> {

//    @JsonCreator
//    public ValueCategory(@JsonProperty("name") @NotEmpty String name, @JsonProperty("qualifier") String qualifier/*, @JsonProperty("locale") Locale locale*/) {
//        this.name = name;
//        this.qualifier = qualifier;
////        this.locale = locale;
//    }

    @Override
    public boolean isMet(String val) {
        return true;
    }

    @Override
    public ConstraintType getType() {
        return CATEGORY;
    }
}
