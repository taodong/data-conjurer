package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Builder
public record PropertyInputControl(
        @NotBlank(message = "Property name is required")String name,
        @Valid List<PropertyValueDistribution> values,
        String defaultValue,
        String referenceStrategy,
        List<@Valid Constraint<?>> constraints
) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyInputControl pic) {
            return StringUtils.equals(name, pic.name());
        }
        return false;
    }
}
