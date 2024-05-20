package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringFormatTest {

    private final ObjectMapper objectMapper = new YAMLMapper();

    private static Stream<Arguments> testYamlCreator() {
        return Stream.of(
                Arguments.of("""
                            type: "format"
                            format: "ref-##?##"
                        """, new StringFormat("ref-##?##")),
                Arguments.of("""
                            type: "format"
                            format: |
                                prefix:
                                    - "prefix-1"
                                    - "prefix-2"
                                suffix:
                                    - "suffix-1"
                                    - "suffix-2"
                        """,
                        new StringFormat(
                                """
                                prefix:
                                    - "prefix-1"
                                    - "prefix-2"
                                suffix:
                                    - "suffix-1"
                                    - "suffix-2"
                                """
                        ))
        );
    }

    @ParameterizedTest
    @MethodSource("testYamlCreator")
    void testYamlCreator(String input, StringFormat expected) throws Exception {
        var actual = objectMapper.readValue(input, Constraint.class);
        assertEquals(expected, actual);
    }

    @Test
    void testGetType() {
        assertEquals(ConstraintType.FORMAT, new StringFormat("format").getType());
    }

}