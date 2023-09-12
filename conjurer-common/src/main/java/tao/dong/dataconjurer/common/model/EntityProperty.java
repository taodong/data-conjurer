package tao.dong.dataconjurer.common.model;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record EntityProperty(@NotBlank(message = "Property name is required") String name,
                             @NotNull(message = "Property type ${validatedValue} is either missing or unsupported") PropertyType type,
                             boolean required, @Min(-1) int idIndex, List<Constraint> constraints,
                             @Valid Reference reference) {
}
