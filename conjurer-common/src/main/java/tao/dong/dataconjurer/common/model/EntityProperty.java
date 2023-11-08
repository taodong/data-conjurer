package tao.dong.dataconjurer.common.model;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public record EntityProperty(@NotBlank(message = "Property name is required") String name,
                             @NotNull(message = "Property type ${validatedValue} is either missing or unsupported") PropertyType type,
                             boolean required, @Min(0) int idIndex, List<Constraint> constraints,
                             @Valid Reference reference) {

    public Set<Constraint> getPropertyConstraints() {
        return constraints == null ? Collections.emptySet() : Set.copyOf(constraints);
    }
}
