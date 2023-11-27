package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.support.EntityTestHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class EntityPropertyTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        return Stream.of(
                Arguments.of(new EntityProperty(null, null, 0, null, null), false),
                Arguments.of(new EntityProperty(" ", TEXT, 0, null, null), false),
                Arguments.of(new EntityProperty("abc", null, 0, null, null), false),
                Arguments.of(new EntityProperty("abc", TEXT,  0, null, new Reference(null, null)), false),
                Arguments.of(new EntityProperty("abc", TEXT,  0, null, null), true),
                Arguments.of(new EntityProperty("abc", TEXT,  0, null, new Reference("efg", "hij")), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(EntityProperty property, boolean passed) {
        var violations = validator.validate(property);
        assertEquals(passed, violations.isEmpty());
    }

    private static Stream<Arguments> testAddConstraints() {
        return Stream.of(
                Arguments.of(EntityTestHelper.entityPropertyBuilder().build(), Collections.emptyList(), 0),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().build(), List.of(new Length(1L)), 1),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().constraints(List.of(new Length(2L))).build(), List.of(new Length(1L)), 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testAddConstraints(EntityProperty property, List<Constraint<?>> extra, int expected) {
        var cloned = property.addConstraints(extra);
        assertEquals(property.name(), cloned.name());
        assertEquals(expected, cloned.constraints().size());
    }

}
