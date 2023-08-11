package tao.dong.dataconjurer.common.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ReferenceTest {

    private static Validator validator;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testNotBlank() {
        return Stream.of(
                Arguments.of(null, "abc"),
                Arguments.of("ebc", null),
                Arguments.of("", "abc"),
                Arguments.of(" ", "   ")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testNotBlank(String entity, String property) {
        var ref = new Reference(entity, property);

        var violations = validator.validate(ref);
        assertFalse(violations.isEmpty());
    }
}
