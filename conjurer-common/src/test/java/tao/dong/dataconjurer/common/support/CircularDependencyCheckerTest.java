package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CircularDependencyCheckerTest {

    private final CircularDependencyChecker checker = new CircularDependencyChecker();

    private static Stream<Arguments> testHasCircular() {
        return Stream.of(
                Arguments.of(Map.of(), false),
                Arguments.of(Map.of("A", Set.of("B", "C")), false),
                Arguments.of(Map.of("A", Set.of("B", "C"), "E", Set.of("H"), "B", Collections.emptySet()), false),
                Arguments.of(Map.of("A", Set.of("A", "E")), true),
                Arguments.of(Map.of("A", Set.of("H"), "B", Set.of("E", "F"), "E", Set.of("A", "K"), "H", Set.of("E")), true)
        );
    }

    @ParameterizedTest
    @MethodSource("testHasCircular")
    void testHasCircular(Map<String, Set<String>> nodes, boolean expected) {
        assertEquals(checker.hasCircular(nodes), expected);
    }
}
