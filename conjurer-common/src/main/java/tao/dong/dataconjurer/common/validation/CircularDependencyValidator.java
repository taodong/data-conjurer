package tao.dong.dataconjurer.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tao.dong.dataconjurer.common.model.DataEntity;
import tao.dong.dataconjurer.common.model.DataSchema;
import tao.dong.dataconjurer.common.support.CircularDependencyChecker;

import java.util.Objects;
import java.util.stream.Collectors;

public class CircularDependencyValidator implements ConstraintValidator<NoCircularDependency, DataSchema> {
    private final CircularDependencyChecker checker = new CircularDependencyChecker();

    @Override
    public boolean isValid(DataSchema schema, ConstraintValidatorContext constraintValidatorContext) {
        var nodes = schema.entities().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        DataEntity::name,
                        entity -> entity.properties().stream()
                                .filter(prop -> prop != null && prop.reference() != null)
                                .map(prop -> prop.reference().entity())
                                .collect(Collectors.toSet())
                ));

        if(checker.hasCircular(nodes)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Circular dependency found for schema " + schema.name()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
