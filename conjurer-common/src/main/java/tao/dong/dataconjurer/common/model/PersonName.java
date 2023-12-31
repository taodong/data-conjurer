package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonName(@NotBlank String value, @NotNull String firstName, @NotNull String lastName) implements CompoundValue {
    @Override
    public String getCategory() {
        return "name";
    }
}
