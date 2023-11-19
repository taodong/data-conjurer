package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.validation.TotalWeightCap;

import java.util.Set;

public record EntityData(@NotBlank(message = "Entity is required") String entity,
                         int dataId,
                         @NotNull(message = "Entity count is required")
                         @Min(value = 0, message = "Entity count ${validatedValue} is less than the allowed minimum {value}") Long count,
                         Set<@Valid @TotalWeightCap PropertyInputControl> properties) {
    public EntityData(String entity, Long count, Set<PropertyInputControl> properties) {
        this(entity, 0, count, properties);
    }
}
