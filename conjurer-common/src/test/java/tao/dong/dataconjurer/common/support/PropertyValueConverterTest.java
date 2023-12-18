package tao.dong.dataconjurer.common.support;

import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static tao.dong.dataconjurer.common.model.PropertyType.SEQUENCE;
import static tao.dong.dataconjurer.common.model.PropertyType.TEXT;

class PropertyValueConverterTest {
    @SuppressWarnings("unchecked")
    @Test
    void testCovertEntityProperties_NonMatchSize() {
        PropertyValueConverter<String> converter = new PropertyValueConverter<String>(mock(BiFunction.class)) {
        };
        assertThrows(IllegalArgumentException.class, () -> converter.convertEntityProperties(
                List.of("abc", "efg"), List.of(TEXT)
        ));
    }

    @Test
    void testConvertEntityProperties() {
        var converter = new PropertyValueConverter<>((o, type) -> type == TEXT ? (String) o : String.valueOf(o)){};
        List<Object> val = List.of("abc", 1L);
        List<PropertyType> types = List.of(TEXT, SEQUENCE);
        var result = converter.convertEntityProperties(val, types);
        assertEquals("abc", result.get(0));
        assertEquals("1", result.get(1));
    }

    @Test
    void testConvertRecords() {
        var converter = new PropertyValueConverter<>((o, type) -> type == TEXT ? (String) o : String.valueOf(o)){};
        List<List<Object>> rows = List.of(List.of("abc", 1L), List.of("eft", 2), List.of("hls", 3));
        List<PropertyType> types = List.of(TEXT, SEQUENCE);
        var result = converter.convertRecords(rows, types);
        assertEquals(3, result.size());
    }

}
