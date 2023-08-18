package tao.dong.dataconjurer.common.model;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public record EntityProperty(@NotBlank(message = "Property name is required") String name,
                             @NotNull(message = "Property type ${validatedValue} is either missing or unsupported") PropertyType type,
                             boolean required, boolean id, List<Constraint> constraints,
                             @Valid Reference reference) {
}
