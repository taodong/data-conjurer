package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringAlternationTest {
    private static Validator validator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testDeserialization() {
        return Stream.of(
                Arguments.of(
                        "{\"type\": \"alternation\", \"properties\": [\"prop1\", \"prop2\"], \"formula\": \"prop1 + prop2\"}",
                        new StringAlternation(Set.of("prop2", "prop1"), "prop1 + prop2")
                ),
                Arguments.of(
                        "{\"type\": \"alternation\", \"properties\": [\"prop1\", \"prop2\", \"prop1\"], \"formula\": \"prop1+ prop2 + prop1\"}",
                        new StringAlternation(Set.of("prop2", "prop1"), "prop1+ prop2 + prop1")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testDeserialization")
    void testDeserialization(String json, StringAlternation expected) throws JsonProcessingException {
        assertEquals(expected, objectMapper.readerFor(StringAlternation.class).readValue(json));
    }

    @Test
    void testValidation() throws JsonProcessingException {
        var nc = objectMapper.readerFor(StringAlternation.class).readValue("{\"type\": \"alternation\", \"properties\": [\"prop1\", \"prop2\"], \"formula\": \"prop1 + prop2\"}");
        var violations = validator.validate(nc);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_FormulaVariableCheck() throws JsonProcessingException {
        var nc = objectMapper.readerFor(StringAlternation.class).readValue("{\"type\": \"alternation\", \"properties\": [\"prop1\", \"prop2\"], \"formula\": \"prop3 + prop2\"}");
        var violations = validator.validate(nc);
        assertEquals(1, violations.size());
        assertTrue(StringUtils.startsWith(violations.iterator().next().getMessage(), "Formula prop3 + prop2"));
    }

    @Test
    void getType() {
        var alternation = new StringAlternation(Set.of("p1", "p2"), "p1 + p2");
        assertEquals(ConstraintType.ALTERNATION, alternation.getType());
    }
}