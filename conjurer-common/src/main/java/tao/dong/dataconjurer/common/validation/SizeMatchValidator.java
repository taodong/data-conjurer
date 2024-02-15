package tao.dong.dataconjurer.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import tao.dong.dataconjurer.common.model.EntityData;
import tao.dong.dataconjurer.common.support.DataHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

public class SizeMatchValidator implements ConstraintValidator<EntryValueSizeMatch, EntityData> {
    private static final ToIntFunction<Collection<?>> GET_SIZE = l -> Optional.ofNullable(l).map(Collection::size).orElse(0);
    @Override
    public boolean isValid(EntityData entityData, ConstraintValidatorContext constraintValidatorContext) {

        if (entityData.entries() != null) {
            var propertySize = GET_SIZE.applyAsInt(entityData.entries().properties());
            var valueNum = GET_SIZE.applyAsInt(entityData.entries().values());
            boolean mismatch = valueNum > 0 ?
                    DataHelper.streamNullableCollection(entityData.entries().values()).mapToInt(List::size).anyMatch(i -> i != propertySize) :
                    propertySize > 0;
            if (mismatch) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "Entity " + entityData.entity() + " has entries with different number between properties and values."
                ).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
