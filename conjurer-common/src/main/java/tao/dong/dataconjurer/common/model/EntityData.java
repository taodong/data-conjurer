package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tao.dong.dataconjurer.common.validation.EntryValueSizeMatch;
import tao.dong.dataconjurer.common.validation.TotalWeightCap;

import java.util.Set;

@EntryValueSizeMatch
public record EntityData(@NotBlank(message = "Entity is required") String entity,
                         int dataId,
                         @NotNull(message = "Entity count is required")
                         @Min(value = 0, message = "Entity count ${validatedValue} is less than the allowed minimum {value}")
                         @Max(value = Integer.MAX_VALUE - 1, message = "Entity count ${validatedValue} is greater than the allowed maximum {value}")
                         Long count,
                         Set<@Valid @TotalWeightCap PropertyInputControl> properties,
                         EntityEntry entries) {
    public EntityData(String entity, Long count, Set<PropertyInputControl> properties, EntityEntry entries) {
        this(entity, 0, count, properties, entries);
    }
}
