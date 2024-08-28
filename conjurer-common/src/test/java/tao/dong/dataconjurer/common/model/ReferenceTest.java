package tao.dong.dataconjurer.common.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferenceTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<Arguments> testNotBlank() {
        return Stream.of(
                Arguments.of(null, "abc", null),
                Arguments.of("ebc", null, "null"),
                Arguments.of("", "abc", null),
                Arguments.of(" ", "   ", "efg")
        );
    }

    @ParameterizedTest
    @MethodSource("testNotBlank")
    void testNotBlank(String entity, String property) {
        var ref = new Reference(entity, property, null);

        var violations = validator.validate(ref);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testHashcodeWithNullLinked() {
        var ref1 = new Reference("abc", "def", null);
        var ref2 = new Reference("abc", "def", null);
        assertEquals(ref1.hashCode(), ref2.hashCode());
    }

    @Test
    void testEqualsWithNullLinked() {
        var ref1 = new Reference("abc", "def", null);
        var ref2 = new Reference("abc", "def", null);
        assertEquals(ref1, ref2);
    }

    @Test
    void testMapKeyWithNullLinked() {
        var ref1 = new Reference("abc", "def", null);
        var ref2 = new Reference("abc", "def", null);
        var map = Map.of(ref1, "abc");
        assertTrue(map.containsKey(ref2));
    }
}
