package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataOutputControlTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testDeserialization() {
        return Stream.of(
                Arguments.of("{\"name\":\"abc\"}", new DataOutputControl("abc", null)),
                Arguments.of("{\"name\":\"abc\", \"entities\": [{\"name\": \"t1\"}]}", new DataOutputControl("abc", Set.of(new EntityOutputControl("t1", null, null))))
        );
    }

    @ParameterizedTest
    @MethodSource("testDeserialization")
    void testDeserialization(String json, DataOutputControl expected) throws JsonProcessingException {
        var result = objectMapper.readerFor(DataOutputControl.class).readValue(json);
        assertEquals(expected, result);
    }

}
