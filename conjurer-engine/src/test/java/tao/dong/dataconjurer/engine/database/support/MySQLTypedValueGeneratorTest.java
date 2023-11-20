package tao.dong.dataconjurer.engine.database.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.support.MutableSequenceGenerator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;

public class MySQLTypedValueGeneratorTest {

    private static Stream<Arguments> testMatchDefaultGeneratorByType() {
        return Stream.of(
                Arguments.of(new EntityProperty("p1", SEQUENCE, 0, null, null), 0L),
                Arguments.of(new EntityProperty("p1", SEQUENCE, 1, null, null), 1L)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testMatchDefaultGeneratorByType(EntityProperty property, long expected) {
        var matcher = new MySQLTypedValueGenerator();
        var result = matcher.matchDefaultGeneratorByType(property);
        assertEquals(expected, ((MutableSequenceGenerator)result).getSequenceGenerator().getCurrent());
    }
}
