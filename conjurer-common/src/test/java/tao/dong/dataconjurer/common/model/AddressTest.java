package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    @Test
    void testGetCategory() {
        var address = new Address(null, null, null, null, null, null);
        assertEquals("address", address.getCategory());
    }
}
