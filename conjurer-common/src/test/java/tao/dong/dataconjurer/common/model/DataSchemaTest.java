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
import static tao.dong.dataconjurer.common.model.Dialect.MYSQL;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

class DataSchemaTest {

    private static Validator validator;

    @SuppressWarnings("resource")
    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        return Stream.of(
              Arguments.of(new DataSchema("  ", MYSQL,
                                          Set.of(new DataEntity("city", Set.of(new EntityProperty("id", SEQUENCE, false, -1, null, null))))), false),
              Arguments.of(new DataSchema("abc", null,
                                          Set.of(new DataEntity("city", Set.of(new EntityProperty("id", SEQUENCE, false, -1, null, null))))), false),
              Arguments.of(new DataSchema("abc", MYSQL, Collections.emptySet()), false),
              Arguments.of(new DataSchema("abc", MYSQL,
                                          Set.of(new DataEntity("city", Set.of(new EntityProperty("id", SEQUENCE, false, -1, null, null))))), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(DataSchema schema, boolean passed) {
        var violations = validator.validate(schema);
        assertEquals(passed, violations.isEmpty());
    }

}
