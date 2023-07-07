package tao.dong.dataconjurer.engine.database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySQLDefaultValueTest {
    private MySQLDefaultValue val = new MySQLDefaultValue();

    @Test
    void testGet() {
        assertEquals("DEFAULT", val.get());
    }
}
