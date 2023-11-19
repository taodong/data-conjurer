package tao.dong.dataconjurer.common.model;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import tao.dong.dataconjurer.common.validation.NoCircularDependency;

import java.util.Set;

@NoCircularDependency
public record DataSchema(
        @NotBlank(message = "Schema name must not be blank") String name,
        @Valid @NotEmpty(message = "Schema entities must not be empty") Set<DataEntity> entities) {
}
