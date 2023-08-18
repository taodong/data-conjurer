package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Set;

@Getter
public record DataEntity(
        @NotBlank(message = "Entity name is required") String name,
        @NotEmpty(message = "At least one property is needed for each entity")
        @Valid Set<EntityProperty> properties) {
}
