package tao.dong.dataconjurer.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.NumberCorrelation;

import java.util.stream.Collectors;

public class FormulaVariableValidator implements ConstraintValidator<FormulaVariableCheck, NumberCorrelation> {
    @Override
    public boolean isValid(NumberCorrelation numberCorrelation, ConstraintValidatorContext constraintValidatorContext) {

        var rs = numberCorrelation.properties().stream()
                .filter(el -> !StringUtils.contains(numberCorrelation.formula(), el))
                .collect(Collectors.toSet());
        if (!rs.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Formula " + numberCorrelation.formula() + " doesn't contain variable(s) " + rs
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
