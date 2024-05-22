package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainedLongGeneratorTest {

    private static Stream<Arguments> testGenerate_Fixed() {
        return Stream.of(
                Arguments.of(0, (BiFunction<Long, Long, Boolean>)(cur, prev) -> cur == (prev - 10L) || cur == (prev + 10L)),
                Arguments.of(1, (BiFunction<Long, Long, Boolean>)(cur, prev) -> cur == prev + 10L),
                Arguments.of(-1, (BiFunction<Long, Long, Boolean>)(cur, prev) -> cur == prev - 10L)
        );
    }

    @ParameterizedTest
    @MethodSource("testGenerate_Fixed")
    void testGenerate_Fixed(int direction, BiFunction<Long, Long, Boolean> checkFun) {
        var generator = new ChainedLongGenerator(direction, 10, 0, 100L);
        long prev = generator.generate();
        for (var i = 0; i < 10; i++) {
            long cur = generator.generate();
            assertTrue(checkFun.apply(cur, prev));
            prev = cur;
        }
    }
}
