package tao.dong.dataconjurer.engine.database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySQLNullValueTest {
    private MySQLNullValue val = new MySQLNullValue();

    @Test
    void testGet() {
        assertEquals("NULL", val.get());
    }
}
