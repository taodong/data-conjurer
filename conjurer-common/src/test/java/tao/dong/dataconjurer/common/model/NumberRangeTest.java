package tao.dong.dataconjurer.common.model;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberRangeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDeserialize() throws JacksonException {
        NumberRange nr = objectMapper.readerFor(NumberRange.class).readValue("{\"type\":\"range\"}");
        assertTrue(nr.isMet(Long.MIN_VALUE));
    }
}
