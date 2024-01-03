package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonName(@NotBlank String value, @NotNull String firstname, @NotNull String lastname) implements CompoundValue {
    @Override
    public String getCategory() {
        return "name";
    }
}
