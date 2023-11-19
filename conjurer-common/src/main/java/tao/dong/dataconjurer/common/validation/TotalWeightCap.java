package tao.dong.dataconjurer.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To validate the total weight listed within PropertyValueDistribution is less than or equal to 1
 */
@Target({ElementType.TYPE_USE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TotalWeightCapValidator.class)
@Documented
@ReportAsSingleViolation
public @interface TotalWeightCap {
    String message() default "Total weight of all PropertyValueDistribution should be less than or equal to 1";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
