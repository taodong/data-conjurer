package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
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

    private static Stream<Arguments> testIsMet() {
        return Stream.of(
                Arguments.of(new CharacterGroup(Set.of("abc")), null, false),
                Arguments.of(new CharacterGroup(null), "abc", false),
                Arguments.of(new CharacterGroup(Set.of("abc", "efg")), "dfa", false),
                Arguments.of(new CharacterGroup(Set.of("abc", "efg")), "efg", true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsMet(CharacterGroup group, String groupName, boolean expected) {
        assertEquals(expected, group.isMet(groupName));
    }
}
