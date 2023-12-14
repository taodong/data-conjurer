package tao.dong.dataconjurer.common.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PropertyInputControlTest {

    @Test
    void testEquals() {
        var pic1 = new PropertyInputControl("p1", null, "default1", "random", null);
        var pic2 = new PropertyInputControl("p1", null, "default2", "loop", null);
        assertEquals(pic1, pic2);
    }

    @Test
    void testEquals_DifferentType() {
        var pic1 = new PropertyInputControl("p1", null, "default1", "random", null);
        Object target = "abc";
        assertNotEquals(pic1, target);
    }
}
