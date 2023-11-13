package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class DataEntityTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        return Stream.of(
                Arguments.of(new DataEntity(" ", null), false),
                Arguments.of(new DataEntity("abc", Collections.emptySet()), false),
                Arguments.of(new DataEntity("abc", Set.of(new EntityProperty(null, null, 0, null, null))), false),
                Arguments.of(new DataEntity("abc", Set.of(new EntityProperty("abc", TEXT, 0, null, null))), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(DataEntity entity, boolean passed) {
        var violations = validator.validate(entity);
        assertEquals(passed, violations.isEmpty());
    }
}
