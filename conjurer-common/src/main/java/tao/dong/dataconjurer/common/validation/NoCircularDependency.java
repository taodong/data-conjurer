package tao.dong.dataconjurer.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CircularDependencyValidator.class)
@Documented
@ReportAsSingleViolation
@SuppressWarnings("unused")
public @interface NoCircularDependency {
    String message() default "Circular dependency found";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
