package tao.dong.dataconjurer.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tao.dong.dataconjurer.common.model.PropertyInputControl;
import tao.dong.dataconjurer.common.model.PropertyValueDistribution;

import java.util.Objects;

public class TotalWeightCapValidator implements ConstraintValidator<TotalWeightCap, PropertyInputControl> {
    @Override
    public boolean isValid(PropertyInputControl propertyInputControl, ConstraintValidatorContext constraintValidatorContext) {
        double totalWeight = propertyInputControl.values()
                .stream().filter(Objects::nonNull)
                .mapToDouble(PropertyValueDistribution::weight)
                .sum();

        if (totalWeight > 1) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Total weight is over 1 for property " + propertyInputControl.name()
            )
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
