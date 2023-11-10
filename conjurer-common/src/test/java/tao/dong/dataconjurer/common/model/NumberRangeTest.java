package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberRangeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDeserialize() throws JsonProcessingException {
        NumberRange nr = objectMapper.readerFor(NumberRange.class).readValue("{\"type\":\"range\"}");
        assertTrue(nr.isMet(Long.MIN_VALUE));
    }
}
