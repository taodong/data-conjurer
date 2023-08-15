package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataPlan(
        @NotBlank(message = "Plan name is required") String name,
        @NotBlank(message = "Schema is required") String schema,
        @Valid @NotEmpty(message = "Data must not be empty") List<@NotNull(message = "data element can't be null") EntityData> data) {
}
