package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotBlank;

public record Reference(@NotBlank(message = "Reference entity is required") String entity,
                        @NotBlank(message = "Reference property is required") String property,
                        String linked) {
}
