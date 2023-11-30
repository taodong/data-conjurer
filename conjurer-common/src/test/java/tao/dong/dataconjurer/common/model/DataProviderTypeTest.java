package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.DataProviderType.ADDRESS;
import static tao.dong.dataconjurer.common.model.DataProviderType.EMAIL;
import static tao.dong.dataconjurer.common.model.DataProviderType.NAME;
import static tao.dong.dataconjurer.common.model.DataProviderType.UNKNOWN;

class DataProviderTypeTest {

    private static Stream<Arguments> testGetByTypeName() {
        return Stream.of(
                Arguments.of(null, UNKNOWN),
                Arguments.of("", UNKNOWN),
                Arguments.of("abc", UNKNOWN),
                Arguments.of("Name", NAME),
                Arguments.of("ADDRESS", ADDRESS),
                Arguments.of("email", EMAIL),
                Arguments.of(" email   ", EMAIL)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetByTypeName(String type, DataProviderType expected) {
        assertEquals(expected, DataProviderType.getByTypeName(type));
    }
}
