package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static tao.dong.dataconjurer.common.model.PropertyType.BOOLEAN;
import static tao.dong.dataconjurer.common.model.PropertyType.DATE;
import static tao.dong.dataconjurer.common.model.PropertyType.DATETIME;
import static tao.dong.dataconjurer.common.model.PropertyType.NUMBER;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class DefaultStringPropertyValueConverterTest {

    private final StringPropertyValueConverter<Object> converter = new DefaultStringPropertyValueConverter();

    private static Stream<Arguments> testConvertValues() {
        return Stream.of(
                Arguments.of(List.of("<?NULL?>",  "<?null?>", " <?Null?>"), SEQUENCE,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(3, converted.size());
                            for (var i = 0; i < 3; i++) {
                                assertNull(converted.get(i));
                            }
                        }
                ),
                Arguments.of(List.of("1", "2", "2.3"), SEQUENCE,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(2, converted.size());
                            assertEquals(1L, converted.get(0));
                            assertEquals(2L, converted.get(1));
                        }
                ),
                Arguments.of(List.of("2.5", "-33.9", "<?NULL?>", "abcd"), NUMBER,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(3, converted.size());
                            assertEquals("2.5", ((BigDecimal)converted.get(0)).toPlainString());
                            assertEquals("-33.9", ((BigDecimal)converted.get(1)).toPlainString());
                            assertNull(converted.get(2));
                        }
                ),
                Arguments.of(List.of("2023-11-17", "2023-11-17 09:22:30", "2023/11/17"), DATE,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(2, converted.size());
                            assertEquals(1700179200000L, (long)converted.get(0));
                            assertEquals(1700179200000L, (long)converted.get(1));
                        }
                ),
                Arguments.of(List.of("2023-11-17", "2023-11-17 00:00:30", "2023/11/17"), DATETIME,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(1, converted.size());
                            assertEquals(1700179230000L, (long)converted.get(0));
                        }
                ),
                Arguments.of(List.of("2023-11-17", "abc", "测试"), TEXT,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(3, converted.size());
                            assertEquals("2023-11-17", converted.get(0));
                            assertEquals("abc", converted.get(1));
                            assertEquals("测试", converted.get(2));
                        }
                ),
                Arguments.of(List.of("true", "FALSE", "True"), BOOLEAN,
                        (Consumer<List<Object>>)(converted) -> {
                            assertEquals(3, converted.size());
                            assertEquals(true, converted.get(0));
                            assertEquals(false, converted.get(1));
                            assertEquals(true, converted.get(2));
                        }
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testConvertValues")
    void testConvertValues(List<String> inputs, PropertyType type, Consumer<List<Object>> verification) {
        verification.accept(converter.convertValues(inputs, type));
    }
}
