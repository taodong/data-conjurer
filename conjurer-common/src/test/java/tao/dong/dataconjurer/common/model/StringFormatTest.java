package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringFormatTest {

    @Test
    void testGetType() {
        assertEquals(ConstraintType.FORMAT, new StringFormat("format").getType());
    }

}