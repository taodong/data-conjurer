package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinkedTypedValueTest {

    private static Stream<Arguments> testAddLinkedValue() {
        return Stream.of(
                Arguments.of("k1", "v1", 1),
                Arguments.of("k2", "v1", 1),
                Arguments.of("k1", "v2", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("testAddLinkedValue")
    void testAddLinkedValue(String key, Object val, int count) {
        var test = new LinkedTypedValue(PropertyType.TEXT, "abc");
        test.addLinkedValue("k1", "v1");
        test.addLinkedValue(key, val);
        assertEquals(count, test.getValues().get(key).size());
    }

    @Test
    void testAddLinkedValue_MismatchedType() {
        var test = new LinkedTypedValue(PropertyType.TEXT, "abc");
        assertThrows(IllegalArgumentException.class, () -> test.addLinkedValue("k1", 1));
    }

    @Test
    void testJoin() {
        var ltv1 = new LinkedTypedValue(PropertyType.TEXT, "abc");
        var ltv2 = new LinkedTypedValue(PropertyType.TEXT, "abc");
        ltv1.addLinkedValue("k1", "v1");
        ltv1.addLinkedValue("k1", "v2");
        ltv1.addLinkedValue("k2", "v2");
        ltv2.addLinkedValue("k1", "v2");
        ltv2.addLinkedValue("k1", "v3");
        ltv2.addLinkedValue("k3", "v1");
        ltv1.join(ltv2);
        assertEquals(3, ltv1.getValues().size());
        assertEquals(3, ltv1.getValues().get("k1").size());
        assertEquals(1, ltv1.getValues().get("k2").size());
        assertEquals(1, ltv1.getValues().get("k3").size());
    }

    @Test
    void testJoin_MismatchedType() {
        var ltv1 = new LinkedTypedValue(PropertyType.TEXT, "abc");
        var ltv2 = new LinkedTypedValue(PropertyType.SEQUENCE, "df");

        assertThrows(IllegalArgumentException.class, () -> ltv1.join(ltv2));
    }

    private static Stream<Arguments> testGetKeyedValues() {
        return Stream.of(
                Arguments.of("k1", 2),
                Arguments.of("k2", 1),
                Arguments.of("k3", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testGetKeyedValues")
    void testGetKeyedValues(String key, int size) {
        var ltv1 = new LinkedTypedValue(PropertyType.TEXT, "ef");
        ltv1.addLinkedValue("k1", "v1");
        ltv1.addLinkedValue("k1", "v2");
        ltv1.addLinkedValue("k2", "v2");
        assertEquals(size, ltv1.getKeyedValues(key).size());
    }

    @Test
    void testGetOrderedValues() {
        var ltv1 = new LinkedTypedValue(PropertyType.TEXT, "ef");
        ltv1.addLinkedValue("k1", "v1");
        ltv1.addLinkedValue("k1", "v2");
        ltv1.addLinkedValue("k2", "v3");
        assertEquals(3, ltv1.getOrderedValues().size());
    }

    @Test
    void testGetOrderedKeys() {
        var ltv1 = new LinkedTypedValue(PropertyType.TEXT, "ef");
        ltv1.addLinkedValue("k1", "v1");
        ltv1.addLinkedValue("k1", "v2");
        ltv1.addLinkedValue("k2", "v3");
        assertEquals(2, ltv1.getOrderedKeys().size());
    }

}
