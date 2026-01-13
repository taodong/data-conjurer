package tao.dong.dataconjurer.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.TimeZone;
import java.time.Year;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tao.dong.dataconjurer.common.model.ConstraintType.TIME_SPAN;

class TimeSpanTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> testJsonCreator() {
        int now = Year.now().getValue();
        return Stream.of(
                // default anchor (omitted) -> anchor is now, leap +1 year
                Arguments.of("{\"type\":\"span\", \"leap\": {\"years\": 1}}", now, now + 1),
                // anchor is now, leap +1 year
                Arguments.of("{\"type\":\"span\",  \"anchor\": \"<?now?>\", \"leap\": {\"years\": 1}}", now, now + 1),
                // explicit anchor parsed from formatted datetime string -> start 2001 end 2002
                Arguments.of("{\"type\":\"span\", \"anchor\": \"2001-01-01 00:00:00\", \"leap\": {\"years\": 1}}", 2001, 2002),
                // explicit anchor with negative leap -> range spans previous year to anchor year
                Arguments.of("{\"type\":\"span\", \"anchor\": \"2002-01-01 00:00:00\", \"leap\": {\"years\": -1}}", 2001, 2002),
                // anchor with months/days to ensure non-year-only leaps still set correct start/end year
                Arguments.of("{\"type\":\"span\", \"anchor\": \"2005-01-01 00:00:00\", \"leap\": {\"months\": 11, \"days\": 30}}", 2005, 2005)
        );
    }

    @ParameterizedTest
    @MethodSource("testJsonCreator")
    void testJsonCreator(String json, int startYear, int endYear) throws JsonProcessingException {
        TimeSpan span = objectMapper.readerFor(TimeSpan.class).readValue(json);
        assertEquals(startYear, getYear(span.getMin()));
        assertEquals(endYear, getYear(span.getMax()));
    }

    private int getYear(long millisecond) {
        var cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(millisecond);
        return cal.get(Calendar.YEAR);
    }

    @Test
    void testGetType() throws JsonProcessingException {
        TimeSpan span = objectMapper.readerFor(TimeSpan.class).readValue("{\"type\":\"span\", \"leap\": {\"seconds\": 1}}");
        assertEquals(TIME_SPAN, span.getType());
    }

    @Test
    void testIsMet() throws JsonProcessingException {
        // anchor at start of 2020, leap +1 year -> should contain a timestamp in April 2020
        var json = "{\"type\":\"span\", \"anchor\": \"2020-01-01 00:00:00\", \"leap\": {\"years\": 1}}";
        TimeSpan span = objectMapper.readerFor(TimeSpan.class).readValue(json);
        var cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 3); // April (0-based months)
        cal.set(Calendar.DAY_OF_MONTH, 1);
        assertTrue(span.isMet(cal.getTimeInMillis()));
    }
}