package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
