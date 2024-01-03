package tao.dong.dataconjurer.common.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonNameTest {

    @Test
    void testGetCategory() {
        var name = new PersonName("Bob Adams", "Bob", "Adams");
        assertEquals("name", name.getCategory());
    }
}
