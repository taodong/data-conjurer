package tao.dong.dataconjurer.common.support;

import org.apache.commons.lang3.SerializationException;
import org.junit.jupiter.api.Test;
import tao.dong.dataconjurer.common.model.PropertyType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyRowSerializerTest {

    // Test the serialize function with a valid input
    @Test
    void serialize() {
        List<Object> row = new ArrayList<>();
        row.add(1L);
        row.add(new BigDecimal("3.2"));
        row.add(1699742737481L);
        row.add(1699742737481L);
        row.add(true);
        row.add("test");
        row.add(null);
        List<PropertyType> properties = List.of(PropertyType.SEQUENCE, PropertyType.NUMBER, PropertyType.DATE, PropertyType.DATETIME, PropertyType.BOOLEAN, PropertyType.TEXT, PropertyType.SEQUENCE);
        List<String> result = PropertyRowSerializer.serialize(row, properties);
        assertThat(result, equalTo(List.of("1", "3.2", "2023-11-11", "2023-11-11 22:45:37", "true", "test", "<?null?>")));
    }

    @Test
    void deserialize() {
        var row = List.of("1", "3.2", "2023-11-11", "2023-11-11 22:45:37", "true", "test", "<?null?>");
        var properties = List.of(PropertyType.SEQUENCE, PropertyType.NUMBER, PropertyType.DATE, PropertyType.DATETIME, PropertyType.BOOLEAN, PropertyType.TEXT, PropertyType.SEQUENCE);
        var result = PropertyRowSerializer.deserialize(row, properties);
        List<Object> expected = new ArrayList<>();
        expected.add(1L);
        expected.add(new BigDecimal("3.2"));
        expected.add(1699660800000L);
        expected.add(1699742737000L);
        expected.add(true);
        expected.add("test");
        expected.add(null);
        assertThat(result, equalTo(expected));
    }

    // Test the serialize function with different number of elements in row and properties and expect a SerializationException
    @Test
    void testSerializeWithDifferentSize() {
        List<Object> row = new ArrayList<>();
        row.add(1L);
        row.add(new BigDecimal("3.2"));
        row.add(1699742737481L);
        row.add(1699742737481L);
        row.add(true);
        row.add("test");
        row.add(null);
        List<PropertyType> properties = List.of(PropertyType.SEQUENCE, PropertyType.NUMBER, PropertyType.DATE, PropertyType.DATETIME, PropertyType.BOOLEAN, PropertyType.TEXT);
        assertThrows(SerializationException.class, () -> PropertyRowSerializer.serialize(row, properties));
    }

    // Test the deserialize function with different number of elements in row and properties and expect a SerializationException
    @Test
    void testDeserializeWithDifferentSize() {
        var row = List.of("1", "3.2", "2023-11-11", "2023-11-11 22:45:37", "true", "test", "<?null?>");
        var properties = List.of(PropertyType.SEQUENCE, PropertyType.NUMBER, PropertyType.DATE, PropertyType.DATETIME, PropertyType.BOOLEAN, PropertyType.TEXT);
        assertThrows(SerializationException.class, () -> PropertyRowSerializer.deserialize(row, properties));
    }

    // Test the deserialize function with a invalid big decimal and expect a SerializationException
    @Test
    void testDeserializeWithInvalidBigDecimal() {
        var row = List.of("1", "3.2a", "2023-11-11", "2023-11-11 22:45:37", "true", "test", "<?null?>");
        var properties = List.of(PropertyType.SEQUENCE, PropertyType.NUMBER, PropertyType.DATE, PropertyType.DATETIME, PropertyType.BOOLEAN, PropertyType.TEXT, PropertyType.SEQUENCE);
        assertThrows(SerializationException.class, () -> PropertyRowSerializer.deserialize(row, properties));
    }

    // Test the serialize function with a invalid date and expect a SerializationException
    @Test
    void testSerializeWithInvalidDate() {
        List<Object> row = new ArrayList<>();
        row.add(1L);
        row.add(new BigDecimal("3.2"));
        row.add("2023-11-11");
        row.add(1699742737481L);
        row.add(true);
        row.add("test");
        row.add(null);
        List<PropertyType> properties = List.of(PropertyType.SEQUENCE, PropertyType.NUMBER, PropertyType.DATE, PropertyType.DATETIME, PropertyType.BOOLEAN, PropertyType.TEXT, PropertyType.SEQUENCE);
        assertThrows(SerializationException.class, () -> PropertyRowSerializer.serialize(row, properties));
    }
}