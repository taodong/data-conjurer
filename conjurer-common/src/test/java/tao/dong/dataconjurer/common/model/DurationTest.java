package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Year;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.model.ConstraintType.DURATION;

class DurationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testJsonCreator() {
        return Stream.of(
                Arguments.of("{\"type\":\"duration\"}", 1000, Year.now().getValue()),
                Arguments.of("{\"type\":\"duration\", \"start\": {\"year\": 2001}}", 2001, Year.now().getValue()),
                Arguments.of("{\"type\":\"duration\", \"end\": {\"year\": 2002}}", 1000, 2002),
                Arguments.of("{\"type\":\"duration\", \"start\": {\"year\": 2005, \"month\": 1, \"day\": 1}, \"end\": {\"year\": 2005, \"month\": 12, \"day\": 31, \"hour\": 3, \"minute\": 15, \"second\": 0}}", 2005, 2005)
        );
    }

    @ParameterizedTest
    @MethodSource("testJsonCreator")
    void testJsonCreator(String json, int startYear, int endYear) throws JsonProcessingException {
        Duration duration = objectMapper.readerFor(Duration.class).readValue(json);
        assertEquals(startYear, getYear(duration.getMin()));
        assertEquals(endYear, getYear(duration.getMax()));
    }

    private int getYear(long millisecond) {
        var cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(millisecond);
        return cal.get(Calendar.YEAR);
    }

    @Test
    void testGetType() {
        var duration = new Duration(new SecondMark(), new SecondMark());
        assertEquals(DURATION, duration.getType());
    }

    @Test
    void testIsMet() {
        var start = new SecondMark();
        start.setYear(2020);
        var end = new SecondMark();
        end.setYear(2021);
        var duration = new Duration(start, end);
        var cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 3);
        assertTrue(duration.isMet(cal.getTimeInMillis()));
    }
}
