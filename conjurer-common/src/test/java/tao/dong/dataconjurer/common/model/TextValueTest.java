package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TextValueTest {

    @Test
    void testGet() {
        var test = new TextValue("abc");
        assertEquals("abc", test.get());
    }

    @Test
    void testEquals() {
        var test1 = new TextValue("abc");
        Object test2 = new TextValue("abc");
        assertEquals(test1, test2);
    }

    @Test
    void testEquals_DifferentText() {
        var test1 = new TextValue("efg");
        var test2 = new TextValue("abc");
        assertNotEquals(test1, test2);
    }

}
