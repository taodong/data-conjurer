package tao.dong.dataconjurer.engine.database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySQLDateValueTest {
    @Test
    void testConstructor() {
        var test = new MySQLDateValue(1699742737481L);
        assertEquals("'2023-11-11'", test.get());
    }
}
