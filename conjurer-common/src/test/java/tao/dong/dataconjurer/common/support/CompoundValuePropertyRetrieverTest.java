package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.CompoundValue;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompoundValuePropertyRetrieverTest {

    private final CompoundValuePropertyRetriever retriever = new CompoundValuePropertyRetriever();

    private static Stream<Arguments> testGetValue() {
        return Stream.of(
                Arguments.of(new compound1Value("abc", 1L), "compound3", "p1", null),
                Arguments.of(new compound2Value(3.5, "abc"), "compound2", "p2", null),
                Arguments.of(new compound1Value("abc", 1L), "compound3", "p1", null),
                Arguments.of(new compound1Value("abc", 1L), "compound1", "p1", "abc"),
                Arguments.of(new compound1Value("abc", 2L), "compound1", "p2", 2L),
                Arguments.of(new compound1Value(null, 2L), "compound1", "p1", null),
                Arguments.of(new compound2Value(3.2, "abc"), "compound2", "p1", 3.2D),
                Arguments.of(new compound2Value(15D, "abc"), "compound2", "p3", "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("testGetValue")
    void testGetValue(CompoundValue obj, String compound, String property, Object expected) {
        assertEquals(expected, retriever.getValue(obj, compound, property));
    }

    record compound1Value(String p1, Long p2) implements CompoundValue {
    }
    record compound2Value(Double p1, String p3) implements CompoundValue {
    }

    @Test
    void testLoadCompoundValueConfiguration() {
        CompoundValuePropertyRetriever retriever2 = new CompoundValuePropertyRetriever();
        retriever2.loadCompoundValueConfiguration(Map.of("extra", Map.of("p1", "test")));
        assertEquals(3, retriever2.getSupported().size());
    }
}
