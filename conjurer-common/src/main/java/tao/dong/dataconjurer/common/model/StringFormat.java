package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;

import static tao.dong.dataconjurer.common.model.ConstraintType.FORMAT;

@JsonTypeName("format")
public record StringFormat(@NotBlank String format) implements Constraint<String> {

    @Override
    public ConstraintType getType() {
        return FORMAT;
    }
}
