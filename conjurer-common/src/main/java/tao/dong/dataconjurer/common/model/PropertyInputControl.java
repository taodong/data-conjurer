package tao.dong.dataconjurer.common.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.validation.TotalWeightCap;

import java.util.List;

public record PropertyInputControl(
        @NotBlank(message = "Property name is required")String name,
        @Valid List<PropertyValueDistribution> values,
        String defaultValue,
        String referenceStrategy
) {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyInputControl pic) {
            return StringUtils.equals(name, pic.name());
        }
        return false;
    }
}
