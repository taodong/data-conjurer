package tao.dong.dataconjurer.common.model;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.JacksonException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testIsMet() {
        return Stream.of(
                Arguments.of(5L, null, 11L, true),
                Arguments.of(7L, -1L, 3L, false),
                Arguments.of(3L, 7L, 10L, true),
                Arguments.of(3L, 7L, 6L, false),
                Arguments.of(3L, 7L, 9L, false),
                Arguments.of(5L, 7L, null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("testIsMet")
    void testIsMet(long leap, Long base, Long val, boolean expected) {
        var interval = new Interval(leap, base);
        assertEquals(expected, interval.isMet(val));
    }

    private static Stream<Arguments> tessDeserialize() {
        return Stream.of(
                Arguments.of("{\"type\":\"interval\", \"leap\": 2}", 1L, 2L),
                Arguments.of("{\"type\":\"interval\", \"leap\": 1, \"base\": 3}", 3L, 1L)
        );
    }

    @ParameterizedTest
    @MethodSource("tessDeserialize")
    void tessDeserialize(String json, long base, long leap) throws JacksonException {
        Interval rs = objectMapper.readerFor(Interval.class).readValue(json);
        assertEquals(base, rs.getBase());
        assertEquals(leap, rs.getLeap());
    }

}
