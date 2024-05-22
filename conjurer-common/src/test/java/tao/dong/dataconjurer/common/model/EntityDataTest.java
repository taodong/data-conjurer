package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityDataTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidate() {
        var entityData = new EntityData(" ", -1L, null, null);
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
                ), null, null, null),
                new PropertyInputControl("t1p1", List.of(
                        new PropertyValueDistribution(Set.of("v4"), 0.9)
                ), null, null, null)
        ), null);
        var violations = validator.validate(entityData);
        assertEquals(1, violations.size());
        assertEquals("Total weight is over 1 for property t1p0", violations.iterator().next().getMessage());
    }


    private static Stream<Arguments> testValidate_Entries() {
        return Stream.of(
                Arguments.of(EntityTestHelper.entityDataBuilder().entries(
                        new EntityEntry(null, List.of(List.of("a")))
                ).build()),
                Arguments.of(EntityTestHelper.entityDataBuilder().entries(
                        new EntityEntry(List.of("p1"), null)
                ).build()),
                Arguments.of(EntityTestHelper.entityDataBuilder().entries(
                        new EntityEntry(List.of("p1"), List.of(List.of("a", "b")))
                ).build()),
                Arguments.of(EntityTestHelper.entityDataBuilder().entries(
                        new EntityEntry(List.of("p1"), List.of(List.of("a"), List.of("e", "f")))
                ).build()),
                Arguments.of(EntityTestHelper.entityDataBuilder().entries(
                        new EntityEntry(List.of("p1", "p2"), List.of(List.of("b", "c"), List.of("a"), List.of("e", "n", "h")))
                ).build())
        );
    }

    @ParameterizedTest
    @MethodSource("testValidate_Entries")
    void testValidate_Entries(EntityData entityData) {
        var violations = validator.validate(entityData);
        assertEquals(1, violations.size());
        assertTrue(StringUtils.equals(violations.iterator().next().getMessage(), "Entity " + entityData.entity() + " has entries with different number between properties and values."));
    }

    private String getPropertyName(PathImpl propertyPath) {
        return propertyPath.getLeafNode().getName();
    }
}
