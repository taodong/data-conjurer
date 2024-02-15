package tao.dong.dataconjurer.engine.database.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tao.dong.dataconjurer.common.model.PropertyType.DATE;
import static tao.dong.dataconjurer.common.model.PropertyType.DATETIME;
import static tao.dong.dataconjurer.common.model.PropertyType.NUMBER;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class MySQLPropertyValueConverterTest {
    private final MySQLPropertyValueConverter converter = new MySQLPropertyValueConverter();

    private static Stream<Arguments> testConvert() {
        return Stream.of(
                Arguments.of(null, SEQUENCE, "NULL"),
                Arguments.of("<?default?>", TEXT, "DEFAULT"),
                Arguments.of(5L, SEQUENCE, "5"),
                Arguments.of(new BigDecimal("7.8"), NUMBER, "7.8"),
                Arguments.of("abc", TEXT, "'abc'"),
                Arguments.of(1699742737481L, DATETIME, "'2023-11-11 22:45:37'"),
                Arguments.of(1699742737481L, DATE, "'2023-11-11'")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testConvert(Object value, PropertyType type, String expected) {
        var textSupplier = converter.convert(value, type);
        assertEquals(expected, textSupplier.getValue());
    }
}
