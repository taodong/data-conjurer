package tao.dong.dataconjurer.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE_USE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SizeMatchValidator.class)
@Documented
@ReportAsSingleViolation
public @interface EntryValueSizeMatch {
    String message() default "The two lists size don't match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
