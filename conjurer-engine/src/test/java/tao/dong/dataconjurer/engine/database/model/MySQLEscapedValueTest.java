package tao.dong.dataconjurer.engine.database.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MySQLEscapedValueTest {

    @Test
    void testGet() {
        var text = new MySQLEscapedValue("abc");
        assertEquals("`abc`", text.get());
    }
}
