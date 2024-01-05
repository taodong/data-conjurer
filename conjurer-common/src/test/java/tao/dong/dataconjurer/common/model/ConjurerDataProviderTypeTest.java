package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.ConjurerDataProviderType.ADDRESS;
import static tao.dong.dataconjurer.common.model.ConjurerDataProviderType.EMAIL;
import static tao.dong.dataconjurer.common.model.ConjurerDataProviderType.NAME;

class ConjurerDataProviderTypeTest {

    private static Stream<Arguments> testGetByTypeName() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", null),
                Arguments.of("abc", null),
                Arguments.of("Name", NAME),
                Arguments.of("ADDRESS", ADDRESS),
                Arguments.of("email", EMAIL),
                Arguments.of(" email   ", EMAIL)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetByTypeName(String type, ConjurerDataProviderType expected) {
        assertEquals(expected, ConjurerDataProviderType.getByTypeName(type));
    }
}
