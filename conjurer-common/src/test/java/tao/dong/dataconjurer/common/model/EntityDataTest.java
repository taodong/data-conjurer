package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityDataTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidate() {
        var entityData = new EntityData(" ", -1L, null);
        var violations = validator.validate(entityData);
        assertEquals(2, violations.size());
        var checked = 0;
        for (var v : violations) {
            if ("count".equals(getPropertyName((PathImpl)v.getPropertyPath()))) {
                assertEquals("Entity count -1 is less than the allowed minimum 0", v.getMessage());
                checked++;
            }
            if ("entity".equals(getPropertyName((PathImpl)v.getPropertyPath()))) {
                assertEquals("Entity is required", v.getMessage());
                checked++;
            }
        }
        assertEquals(2, checked);
    }

    @Test
    void testValidate_TotalWeightCap() {
        var entityData = new EntityData("t1", 1L, Set.of(
                new PropertyInputControl("t1p0", List.of(
                        new PropertyValueDistribution(Set.of("v1"), 0.9),
                        new PropertyValueDistribution(Set.of("v2", "v3"), 0.2)
                ), null, null),
                new PropertyInputControl("t1p1", List.of(
                        new PropertyValueDistribution(Set.of("v4"), 0.9)
                ), null, null)
        ));
        var violations = validator.validate(entityData);
        assertEquals(1, violations.size());
        assertEquals("Total weight is over 1 for property t1p0", violations.iterator().next().getMessage());
    }

    private String getPropertyName(PathImpl propertyPath) {
        return propertyPath.getLeafNode().getName();
    }
}
