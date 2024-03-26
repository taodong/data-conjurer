package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.Dialect.GENERAL;
import static tao.dong.dataconjurer.common.model.Dialect.MYSQL;
import static tao.dong.dataconjurer.common.model.Dialect.SALESFORCE;
import static tao.dong.dataconjurer.common.model.PropertyType.BOOLEAN;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TIME;

class PropertyTypeTest {

    private static Stream<Arguments> testIsSupport() {
        return Stream.of(
                Arguments.of(SEQUENCE, MYSQL, true),
                Arguments.of(SEQUENCE, GENERAL, true),
                Arguments.of(BOOLEAN, MYSQL, false),
                Arguments.of(BOOLEAN, GENERAL, true),
                Arguments.of(BOOLEAN, SALESFORCE, true),
                Arguments.of(TIME, MYSQL, true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testIsSupport(PropertyType type, Dialect dialect, boolean expected) {
        assertEquals(expected, type.getSupport().test(dialect));
    }
}
