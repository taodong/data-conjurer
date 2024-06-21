package tao.dong.dataconjurer.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import tao.dong.dataconjurer.common.model.FormulaConstraint;

import java.util.stream.Collectors;

public class FormulaVariableValidator implements ConstraintValidator<FormulaVariableCheck, FormulaConstraint> {
    @Override
    public boolean isValid(FormulaConstraint formulaConstraint, ConstraintValidatorContext constraintValidatorContext) {

        var rs = formulaConstraint.properties().stream()
                .filter(el -> !StringUtils.contains(formulaConstraint.formula(), el))
                .collect(Collectors.toSet());
        if (!rs.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Formula " + formulaConstraint.formula() + " doesn't contain variable(s) " + rs
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
