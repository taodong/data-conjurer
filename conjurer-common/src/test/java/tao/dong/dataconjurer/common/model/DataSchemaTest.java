package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

class DataSchemaTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        return Stream.of(
              Arguments.of(new DataSchema("  ",
                                          Set.of(new DataEntity("city", Set.of(new EntityProperty("id", SEQUENCE, 0, null, null))))), false),
              Arguments.of(new DataSchema("abc", Collections.emptySet()), false),
              Arguments.of(new DataSchema("abc",
                                          Set.of(new DataEntity("city", Set.of(new EntityProperty("id", SEQUENCE, 0, null, null))))), true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testValidate(DataSchema schema, boolean passed) {
        var violations = validator.validate(schema);
        assertEquals(passed, violations.isEmpty());
    }

    @Test
    void testCircularDependencies() {
        var schema = new DataSchema("schema1", Set.of(
                new DataEntity("t1", Set.of(
                   new EntityProperty("t1p0", SEQUENCE, 1, null, null),
                   new EntityProperty("t1p1", SEQUENCE, 0, null, new Reference("t2", "t2p0"))
                )),
                new DataEntity("t2", Set.of(
                        new EntityProperty("t2p0", SEQUENCE, 1, null, null),
                        new EntityProperty("t2p1", SEQUENCE, 0, null, new Reference("t3", "t3p0"))
                )),
                new DataEntity("t3", Set.of(
                        new EntityProperty("t3p0", SEQUENCE, 1, null, null),
                        new EntityProperty("t3p1", SEQUENCE, 0, null, new Reference("t1", "t1p0"))
                ))
        ));
        var violations = validator.validate(schema);
        assertEquals(1, violations.size());
        assertEquals("Circular dependency found for schema schema1", violations.iterator().next().getMessage());
    }

}
