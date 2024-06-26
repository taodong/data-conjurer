package tao.dong.dataconjurer.engine.database.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.EntityIndex;
import tao.dong.dataconjurer.common.model.EntityProperty;
import tao.dong.dataconjurer.common.service.DataProviderService;
import tao.dong.dataconjurer.common.support.FormattedTextGenerator;
import tao.dong.dataconjurer.common.support.MutableSequenceGenerator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class MySQLSimpleTypedValueGeneratorTest {

    private final DataProviderService dataProviderApi = mock(DataProviderService.class);

    private static Stream<Arguments> testMatchDefaultGeneratorByType() {
        return Stream.of(
                Arguments.of(new EntityProperty("p1", SEQUENCE, null, null, null), 1L),
                Arguments.of(new EntityProperty("p1", SEQUENCE, new EntityIndex(0, 0, 0), null, null), 1L)
        );
    }

    @ParameterizedTest
    @MethodSource("testMatchDefaultGeneratorByType")
    void testMatchDefaultGeneratorByType(EntityProperty property, long expected) {
        var matcher = new MySQLTypedValueGenerator();
        var result = matcher.matchDefaultGeneratorByType(property, dataProviderApi);
        assertEquals(expected, ((MutableSequenceGenerator)result).getSequenceGenerator().getCurrent());
    }

    @Test
    void testMatchDefaultGeneratorByType_NoneSequence() {
        var matcher = new MySQLTypedValueGenerator();
        var result = matcher.matchDefaultGeneratorByType(new EntityProperty("p1", TEXT, null, null, null), dataProviderApi);
        assertTrue(result instanceof FormattedTextGenerator);
    }
}
