package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailTest {
    @Test
    void testGetCategory() {
        var email = new Email("abc");
        assertEquals("email", email.getCategory());
    }
}
