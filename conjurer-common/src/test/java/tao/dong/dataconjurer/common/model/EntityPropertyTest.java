package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class EntityPropertyTest {

    private static Validator validator;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        return Stream.of(
                Arguments.of(new EntityProperty(null, null, false, -1, null, null), false),
                Arguments.of(new EntityProperty(" ", TEXT, true, -1, null, null), false),
                Arguments.of(new EntityProperty("abc", null, false, -1, null, null), false),
                Arguments.of(new EntityProperty("abc", TEXT, false, -1, null, new Reference(null, null)), false),
                Arguments.of(new EntityProperty("abc", TEXT, false, -1, null, null), true),
                Arguments.of(new EntityProperty("abc", TEXT, false, -1, null, new Reference("efg", "hij")), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(EntityProperty property, boolean passed) {
        var violations = validator.validate(property);
        assertEquals(passed, violations.isEmpty());
    }

}
