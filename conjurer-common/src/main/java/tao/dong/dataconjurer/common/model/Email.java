package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.NotBlank;

public record Email(@NotBlank String value) implements CompoundValue {
    @Override
    public String getCategory() {
        return "email";
    }
}
