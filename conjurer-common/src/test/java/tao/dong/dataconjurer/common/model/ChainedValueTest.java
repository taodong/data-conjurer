package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.model.ConstraintType.CHAIN;

public class ChainedValueTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testDeserialize() {
        return Stream.of(
                Arguments.of("{\"type\": \"chain\", \"seed\": 3.5}", 3.5, 0, 0),
                Arguments.of("{\"type\": \"chain\", \"seed\": 2, \"direction\": -1}", 2, -1, 0),
                Arguments.of("{\"type\": \"chain\", \"seed\": 3.6, \"direction\": 1, \"style\": 2}", 3.6, 1, 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testDeserialize(String json, double seed, int direction, int style) throws JsonProcessingException {
        ChainedValue rs = objectMapper.readerFor(ChainedValue.class).readValue(json);
        assertEquals(seed, rs.getSeed());
        assertEquals(direction, rs.getDirection());
        assertEquals(style, rs.getStyle());
    }

    @Test
    void testIsMet() {
        var rs = new ChainedValue(3.2, 0, 0);
        assertTrue(rs.isMet(100D));
    }

    @Test
    void testGetType() {
        var rs = new ChainedValue(3.2, 1, 0);
        assertEquals(CHAIN, rs.getType());
    }
}
