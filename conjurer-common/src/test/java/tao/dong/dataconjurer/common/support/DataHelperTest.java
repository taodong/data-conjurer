package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataHelperTest {

    @Test
    void testAppendToSetValueInMap() {
        Map<String, Set<String>> test = new HashMap<>();
        DataHelper.appendToSetValueInMap(test, "k1", "v1");
        assertEquals(1, test.get("k1").size());
        DataHelper.appendToSetValueInMap(test, "k1", "v2");
        assertEquals(1, test.size());
        assertEquals(2, test.get("k1").size());
    }

    private static Stream<Arguments> testFormatMilliseconds() {
        return Stream.of(
                Arguments.of(1699742737481L, "yyyy-MM-dd", "2023-11-11"),
                Arguments.of(1699742737481L, "yyyy-MM-dd HH:mm:ss", "2023-11-11 22:45:37")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testFormatMilliseconds(long milliseconds, String pattern, String expected) {
        assertEquals(expected, DataHelper.formatMilliseconds(milliseconds, pattern));
    }

    @Test
    void testRemoveIndexFromList() {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(3);
        list.add(7);
        list.add(11);
        var indexes = Set.of(0, 3);
        var rs = DataHelper.removeIndexFromList(list, indexes);
        assertEquals(2, rs.size());
        assertEquals(10, rs.stream().reduce(0, Integer::sum));
    }

    private static Stream<Arguments> testStreamNullableCollection() {
        return Stream.of(
                Arguments.of(null, 0),
                Arguments.of(List.of(1, 2), 2)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testStreamNullableCollection(Collection<Integer> col, int expected) {
        Set<Integer> res = DataHelper.streamNullableCollection(col).collect(Collectors.toSet());
        assertEquals(expected, res.size());
    }

    private static Stream<Arguments> testConvertTimeStringToSecond() {
        return Stream.of(
                Arguments.of("00:00:00", 0L),
                Arguments.of("01:00:00", 3600L),
                Arguments.of("01:01:01", 3661L),
                Arguments.of("-23:59:59", -86399L),
                Arguments.of("838:59:59", TimeGenerator.PLUS_839_HOURS - 1),
                Arguments.of("-838:59:59", TimeGenerator.MINUS_839_HOURS)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testConvertTimeStringToSecond(String str, long expected) {
        assertEquals(expected, DataHelper.convertTimeStringToSecond(str));
    }

    @ParameterizedTest
    @ValueSource(strings = {"00:00", "00:00:00:00", "00:00:00:00:00", "00:00:60", "00:60:00"})
    void testConvertTimeStringToSecondInvalid(String invalidStr) {
        assertThrows(NumberFormatException.class, () -> DataHelper.convertTimeStringToSecond(invalidStr));
    }

    private static Stream<Arguments> testFormatTimeInSeconds() {
        return Stream.of(
                Arguments.of(0L, "00:00:00"),
                Arguments.of(3600L, "01:00:00"),
                Arguments.of(3661L, "01:01:01"),
                Arguments.of(86399L, "23:59:59"),
                Arguments.of(86400L, "24:00:00"),
                Arguments.of(-86400L, "-24:00:00"),
                Arguments.of(TimeGenerator.PLUS_839_HOURS - 1, "838:59:59"),
                Arguments.of(TimeGenerator.MINUS_839_HOURS, "-838:59:59")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testFormatTimeInSeconds(long seconds, String expected) {
        assertEquals(expected, DataHelper.formatTimeInSeconds(seconds));
    }

    private static Stream<Arguments> testOutputLongValue() {
        return Stream.of(
                Arguments.of(5L, 5L),
                Arguments.of(5, 5L),
                Arguments.of(5.0, 5L),
                Arguments.of(5.1, 5L),
                Arguments.of(5.9, 5L),
                Arguments.of(5.999, 5L),
                Arguments.of(BigDecimal.valueOf(5), 5L)
        );

    }

    @ParameterizedTest
    @MethodSource
    void testOutputLongValue(Object val, Long expected) {
        assertEquals(expected, DataHelper.outputLongValue(val));
    }

    @Test
    void testOutputLongValueInvalid() {
        assertThrows(NumberFormatException.class, () -> DataHelper.outputLongValue("5"));
    }
}
