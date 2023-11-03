package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EntityData(@NotBlank(message = "Entity is required") String entity,
                         int dataId,
                         @NotNull(message = "Entity count is required")
                         @Min(value = 0, message = "Entity count ${validatedValue} is less than the allowed minimum {value}") Long count) {
    public EntityData(String entity, Long count) {
        this(entity, 0, count);
    }
}
