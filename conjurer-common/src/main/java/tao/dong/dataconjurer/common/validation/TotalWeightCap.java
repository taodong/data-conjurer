package tao.dong.dataconjurer.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TotalWeightCapValidator.class)
@Documented
public @interface TotalWeightCap {
    String message() default "Total weight of all PropertyValueDistribution should be less than or equal to 1";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
