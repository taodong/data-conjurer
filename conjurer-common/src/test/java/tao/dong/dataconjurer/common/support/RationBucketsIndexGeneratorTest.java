package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.RatioRange;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RationBucketsIndexGeneratorTest {

    private static Stream<Arguments> testMatchBucket() {
        var generator1 = new RatioBucketsIndexGenerator();
        var generator2 = new RatioBucketsIndexGenerator(new RatioRange(0.0, 0.9));
        var generator3 = new RatioBucketsIndexGenerator(new RatioRange(0.7, 1.0), new RatioRange(0.5, 0.7), new RatioRange(0.0, 0.5));
        var generator4 = new RatioBucketsIndexGenerator(new RatioRange(0.0, 0.45), new RatioRange(0.45, 0.88));
        return Stream.of(
                Arguments.of(generator1, 0.3, -1),
                Arguments.of(generator2, 0.11, 0),
                Arguments.of(generator2, 0.9, 0),
                Arguments.of(generator2, 0.91, -1),
                Arguments.of(generator3, 0.8, 0),
                Arguments.of(generator3, 0.59, 1),
                Arguments.of(generator3, 0.23, 2),
                Arguments.of(generator4, 0.45, 0),
                Arguments.of(generator4, 0.87, 1),
                Arguments.of(generator4, 0.9, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("testMatchBucket")
    void testMatchBucket(RatioBucketsIndexGenerator generator, double key, int expected) {
        assertEquals(expected, generator.matchBucket(key));
    }
}
