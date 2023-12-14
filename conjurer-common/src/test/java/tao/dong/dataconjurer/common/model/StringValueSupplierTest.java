package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringValueSupplierTest {
    @Test
    void testGet() {
        var test = new StringValueSupplier<>(1) {};
        assertEquals("1", test.get());
    }

}
