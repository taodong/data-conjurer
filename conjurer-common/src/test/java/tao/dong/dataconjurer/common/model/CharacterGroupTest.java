package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterGroupTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testJsonCreator() {
        return Stream.of(
                Arguments.of("{\"type\": \"char_group\", \"groups\": [\"abc\"]}", 1),
                Arguments.of("{\"type\": \"char_group\", \"groups\": [\"abc\", \"abc\"]}", 1),
                Arguments.of("{\"type\": \"char_group\", \"groups\": [\"abc\", \"efg\"]}", 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testJsonCreator(String json, int expected) throws JsonProcessingException {
        CharacterGroup group = objectMapper.readerFor(CharacterGroup.class).readValue(json);
        assertEquals(expected, group.groups().size());
    }
}
