package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.Locale;

import static tao.dong.dataconjurer.common.model.ConstraintType.CATEGORY;

@JsonTypeName("category")
public record ValueCategory (@NotEmpty String name, String qualifier, Locale locale, @Min(0) int compoundId) implements Constraint<String> {

    @Override
    public ConstraintType getType() {
        return CATEGORY;
    }

    public String getValueId() {
        return name + '_' + compoundId;
    }
}
