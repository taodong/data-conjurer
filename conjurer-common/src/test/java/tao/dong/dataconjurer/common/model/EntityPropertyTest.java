package tao.dong.dataconjurer.common.model;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.support.EntityTestHelper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class EntityPropertyTest {

    private static Validator validator;
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testValidate() {
        return Stream.of(
                Arguments.of(EntityTestHelper.entityPropertyBuilder().name(null).type(null).build(),
                        false),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().name(" ").type(TEXT).build(),
                        false),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().name("abc").type(null).build(),
                        false),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().name("abc").type(TEXT).reference(new Reference(null, null, null)).build(),
                        false),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().name("abc").type(TEXT).build(),
                        true),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().name("abc").type(TEXT).reference(new Reference("efg", "hij", null)).build(),
                        true)
        );
    }

    @ParameterizedTest
    @MethodSource("testValidate")
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
    @MethodSource("testAddConstraints")
    void testAddConstraints(EntityProperty property, List<Constraint<?>> extra, int expected) {
        var cloned = property.addConstraints(extra);
        assertEquals(property.name(), cloned.name());
        assertEquals(expected, cloned.constraints().size());
    }

    private static Stream<Arguments> testDeserialization() {
        return Stream.of(
                Arguments.of("{\"name\": \"t1\", \"type\": \"sequence\"}", EntityTestHelper.entityPropertyBuilder().name("t1").constraints(null).build()),
                Arguments.of("{\"name\": \"t2\", \"type\": \"text\", \"constraints\": []}", EntityTestHelper.entityPropertyBuilder().name("t2").type(TEXT).build()),
                Arguments.of("{\"name\": \"t2\", \"type\": \"text\", \"constraints\": [{\"type\": \"char_group\", \"groups\": [\"abc\"]}]}", EntityTestHelper.entityPropertyBuilder().name("t2").type(TEXT).constraints(List.of(new CharacterGroup(Set.of("abc")))).build()),
                Arguments.of("{\"name\": \"t2\", \"type\": \"text\", \"constraints\": [{\"type\": \"char_group\", \"groups\": [\"abc\"]}], \"index\": {\"id\": 0}, \"reference\": {\"entity\": \"t3\", \"property\": \"p1\"}}",
                        EntityTestHelper.entityPropertyBuilder().name("t2").type(TEXT)
                                .constraints(List.of(new CharacterGroup(Set.of("abc"))))
                                .reference(new Reference("t3", "p1", null))
                                .index(EntityTestHelper.entityIndexBuilder().build())
                                .build()
                ),
                Arguments.of("{\"name\": \"t2\", \"type\": \"text\", \"constraints\": [{\"type\": \"char_group\", \"groups\": [\"abc\"]}], \"reference\": {\"entity\": \"t3\", \"property\": \"p1\", \"linked\": \"p2\"}, \"index\": {\"id\": 1, \"type\": 1, \"qualifier\": 1}}",
                        EntityTestHelper.entityPropertyBuilder().name("t2").type(TEXT)
                                .constraints(List.of(new CharacterGroup(Set.of("abc"))))
                                .reference(new Reference("t3", "p1", "p2"))
                                .index(EntityTestHelper.entityIndexBuilder().id(1).type(1).qualifier(1).build())
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testDeserialization")
    void testDeserialization(String json, EntityProperty expected) throws JacksonException {
        assertEquals(expected, objectMapper.readerFor(EntityProperty.class).readValue(json));
    }

    private static Stream<Arguments> testGetPropertyConstraints() {
        return Stream.of(
                Arguments.of(EntityTestHelper.entityPropertyBuilder().constraints(null).build(), 0),
                Arguments.of(EntityTestHelper.entityPropertyBuilder().constraints(List.of(new Length(5L))).build(), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("testGetPropertyConstraints")
    void testGetPropertyConstraints(EntityProperty ep, int size) {
        assertEquals(size, ep.getPropertyConstraints().size());
    }
}
