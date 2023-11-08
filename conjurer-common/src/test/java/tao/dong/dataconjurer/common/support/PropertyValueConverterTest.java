package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class PropertyValueConverterTest {
    @SuppressWarnings("unchecked")
    private final PropertyValueConverter<String> converter = new PropertyValueConverter<String>(mock(BiFunction.class)) {
    };

    @Test
    void testCovertEntityProperties_NonMatchSize() {
        assertThrows(IllegalArgumentException.class, () -> converter.convertEntityProperties(
                List.of("abc", "efg"), List.of(TEXT)
        ));
    }
}
