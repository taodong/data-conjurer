package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityDataTest {

    private static Validator validator;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidate() {
        var entityData = new EntityData(" ", -1L);
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

    private String getPropertyName(PathImpl propertyPath) {
        return propertyPath.getLeafNode().getName();
    }
}
