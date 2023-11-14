package tao.dong.dataconjurer.common.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record PropertyValueDistribution(@NotEmpty(message="At least one value is needed for entity value distribution") Set<String> values,
                                        @Positive(message="weight has to be a positive number")
                                        @DecimalMax(value = "1", message = "weight value has to be less or equal to 1") Double weight) {
}
