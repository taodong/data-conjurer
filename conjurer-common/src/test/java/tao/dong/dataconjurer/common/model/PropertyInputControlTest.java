package tao.dong.dataconjurer.common.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyInputControlTest {

    @Test
    void testEquals() {
        var pic1 = new PropertyInputControl("p1", null, "default1", "random");
        var pic2 = new PropertyInputControl("p1", null, "default2", "loop");
        assertEquals(pic1, pic2);
    }
}
